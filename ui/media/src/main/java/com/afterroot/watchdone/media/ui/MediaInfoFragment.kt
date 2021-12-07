/*
 * Copyright (C) 2020-2021 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.watchdone.media.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.core.extensions.showStaticProgressDialog
import com.afterroot.core.extensions.visible
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.Multi.MediaType.MOVIE
import com.afterroot.tmdbapi.model.Multi.MediaType.PERSON
import com.afterroot.tmdbapi.model.Multi.MediaType.TV_SERIES
import com.afterroot.tmdbapi.model.Multi.MediaType.valueOf
import com.afterroot.tmdbapi2.model.Genre
import com.afterroot.tmdbapi2.model.MovieAppendableResponses
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.tmdbapi2.repository.TVRepository
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toDBMedia
import com.afterroot.watchdone.data.mapper.toMovie
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.media.R
import com.afterroot.watchdone.media.adapter.CastListAdapter
import com.afterroot.watchdone.media.databinding.FragmentMediaInfoBinding
import com.afterroot.watchdone.media.viewmodel.MediaInfoViewModel
import com.afterroot.watchdone.media.viewmodel.SelectedMedia
import com.afterroot.watchdone.media.viewmodel.State
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.getField
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import java.util.Locale

@AndroidEntryPoint
class MediaInfoFragment : Fragment() {
    private lateinit var binding: FragmentMediaInfoBinding
    private lateinit var watchlistItemReference: CollectionReference
    private lateinit var watchListRef: DocumentReference
    private val firebaseUtils: FirebaseUtils by inject()
    private val moviesRepository: MoviesRepository by inject()
    private val myDatabase: MyDatabase by inject()
    private val settings: Settings by inject()
    private val tvRepository: TVRepository by inject()
    private val viewModel: MediaInfoViewModel by activityViewModels()
    private var progressDialog: MaterialDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentMediaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argMediaId = arguments?.getInt("mediaId")
        val mediaType = arguments?.getString("type")?.let { valueOf(it.uppercase(Locale.getDefault())) }
        if (argMediaId != null) {
            launchShowingProgress {
                when (mediaType) {
                    MOVIE -> {
                        viewModel.selectMedia(movie = getInfoFromServerForCompare(argMediaId), tv = null)
                        updateCast(argMediaId, mediaType)
                    }
                    PERSON -> {
                        // TODO
                    }
                    TV_SERIES -> {
                        viewModel.selectMedia(
                            movie = null, tv = tvRepository.getTVInfo(argMediaId).toTV()
                        )
                        updateCast(argMediaId, mediaType)
                    }
                    null -> {
                    }
                }
            }
        }

        // Refreshes Ui When Actions clicked
        viewModel.getWatchlistSnapshot(firebaseUtils.uid!!).observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Error<*> -> {
                }
                is State.Loaded<*> -> {
                    viewModel.getSelectedMedia().observe(viewLifecycleOwner) {
                        when (it) {
                            is SelectedMedia.Movie -> {
                                Log.d(TAG, "onViewCreated: ${it.data}")
                                updateUI(movie = it.data)
                            }
                            is SelectedMedia.TV -> {
                                Log.d(TAG, "onViewCreated: ${it.data}")
                                updateUI(tv = it.data)
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private val similarMovieItemSelectedCallback = object : ItemSelectedCallback<Movie> {
        override fun onClick(position: Int, view: View?, item: Movie) {
            super.onClick(position, view, item)
            // homeViewModel.selectMovie(item)
            // view?.findNavController()?.navigate(R.id.movieInfoToMovieInfo)
        }
    }

    private val similarTVItemSelectedCallback = object : ItemSelectedCallback<TV> {
        override fun onClick(position: Int, view: View?, item: TV) {
            super.onClick(position, view, item)
            // homeViewModel.selectMovie(item)
            // view?.findNavController()?.navigate(R.id.movieInfoToMovieInfo)
        }
    }

    private fun updateUI(movie: Movie? = null, tv: TV? = null) { // Do operations related to database
        val id = movie?.id ?: tv?.id
        val posterPath = movie?.posterPath ?: tv?.posterPath
        val posterUrl = posterPath?.let { settings.createPosterUrl(it) }
        val title = movie?.title ?: tv?.name
        val description = movie?.overview ?: tv?.overview
        val genres = movie?.genres ?: tv?.genres
        val genresIds = movie?.genreIds
        watchListRef = get<FirebaseFirestore>().collectionWatchdone(
            id = get<FirebaseAuth>().currentUser?.uid.toString(),
            isUseOnlyProdDB = settings.isUseProdDb
        ).document(Collection.WATCHLIST)
        watchlistItemReference = watchListRef.collection(Collection.ITEMS)
        binding.apply {
            settings = this@MediaInfoFragment.settings
            // updateGenres(movie) //todo
            this.posterUrl = posterUrl
            this.title = title
            this.description = description
            // executePendingBindings()
            watchlistItemReference.whereEqualTo(Field.ID, id).get(Source.CACHE).addOnSuccessListener {
                kotlin.runCatching { // Fix crash if user quickly press back button just after navigation
                    val isInWatchlist = it.documents.size > 0
                    var isWatched = false
                    var selectedMediaDocId: String? = null
                    if (isInWatchlist) { // Get Watching Status if in Watchlist
                        val document = it.documents[0]
                        document.getBoolean(Field.IS_WATCHED)?.let { watched ->
                            isWatched = watched
                        }
                        selectedMediaDocId = document.id
                    }
                    launchShowingProgress { // Update Genres
                        updateGenres(genres, genresIds)
                        hideProgress()
                    }
                    actionAddWlist.apply {
                        visible(true)
                        if (!isInWatchlist) {
                            text = getString(R.string.text_add_to_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark_border)
                            setOnClickListener {
                                doShowingProgress {
                                    progressDialog = requireContext().showStaticProgressDialog("Please Wait...")
                                    addToWatchlist(movie, tv)
                                }
                            }
                        } else {
                            text = getString(R.string.text_remove_from_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark)
                            setOnClickListener {
                                selectedMediaDocId?.let { id -> watchlistItemReference.document(id).delete() }
                                watchListRef.updateTotalItemsCounter(-1)
                                snackBarMessage(requireContext().getString(R.string.msg_removed_from_wl))
                            }
                        }
                    }
                    actionMarkWatched.apply {
                        visible(true)
                        if (isInWatchlist && selectedMediaDocId != null) {
                            if (isWatched) {
                                text = getString(R.string.text_mark_as_unwatched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_clear)
                            } else {
                                text = getString(R.string.text_mark_as_watched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_done)
                            }
                            setOnClickListener {
                                watchlistItemReference.document(selectedMediaDocId)
                                    .update(Field.IS_WATCHED, !isWatched)
                            }
                        } else {
                            setOnClickListener {
                                snackBarMessage(requireContext().getString(R.string.msg_add_to_wl_first))
                            }
                        }
                    }

/*
                    composeView.setContent {
                        Column {
                            if (movie != null) {
                                SimilarMovies(movie, lifecycleScope, moviesRepository, similarMovieItemSelectedCallback)
                            }
                            if (tv != null) {
                                SimilarTV(tv, lifecycleScope, tvRepository, similarTVItemSelectedCallback)
                            }
                        }
                    }
*/
                }
            }
            // menu?.findItem(R.id.action_view_imdb)?.isVisible = !binding.movie?.imdbId.isNullOrBlank()
        }
    }

    private fun updateGenres(list: List<Genre>? = emptyList(), ids: List<Int>? = null) {
        if (list?.isEmpty() == true) {
            ids?.let {
                myDatabase.genreDao().getGenres(it).observe(viewLifecycleOwner) { roomGenres ->
                    binding.genres = roomGenres
                }
            }
        } else {
            binding.genres = list
        }
    }

    // TODO Apply Same for TV
    private suspend fun updateCast(mediaId: Int, type: Multi.MediaType) {
        val credits = if (type == MOVIE) {
            get<MoviesRepository>().getCredits(mediaId)
        } else {
            get<TVRepository>().getCredits(mediaId)
        }

        val castAdapter = CastListAdapter()
        val crewAdapter = CastListAdapter()
        binding.castList.apply {
            adapter = castAdapter
            visible(true, AutoTransition())
        }

        binding.crewList.apply {
            adapter = crewAdapter
            visible(true, AutoTransition())
        }
        castAdapter.submitList(credits.cast)
        crewAdapter.submitList(credits.crew)
    }

    private fun addToWatchlist(movie: Movie? = null, tv: TV? = null) {
        if (movie != null) {
            watchlistItemReference.add(movie.toDBMedia())
        } else if (tv != null) {
            watchlistItemReference.add(tv.toDBMedia())
        }
        watchListRef.updateTotalItemsCounter(1)
        snackBarMessage(requireContext().getString(R.string.msg_added_to_wl))
        hideProgress()
    }

    private suspend fun getInfoFromServer(id: Int) = withContext(Dispatchers.IO) {
        moviesRepository.getFullMovieInfo(id, MovieAppendableResponses.credits).toMovie()
    }

    private suspend fun getInfoFromServerForCompare(id: Int) = withContext(Dispatchers.IO) {
        moviesRepository.getMovieInfo(id).toMovie()
    }

    // Helper Functions

    private fun doShowingProgress(task: () -> Unit) {
        binding.progressMovieInfo.apply {
            if (visibility == View.GONE) {
                visible(true, AutoTransition())
            }
        }
        task()
    }

    private fun launchShowingProgress(task: suspend () -> Unit) {
        doShowingProgress {
            lifecycleScope.launch {
                task()
            }
        }
    }

    private fun hideProgress() {
        binding.progressMovieInfo.apply {
            if (visibility == View.VISIBLE) {
                visible(false, AutoTransition())
            }
        }
        progressDialog?.dismiss()
    }

    private fun snackBarMessage(message: String) {
        // TODO Use ViewModel
    }

    // Extension Functions

    private fun Int.toHex() = "#${Integer.toHexString(this)}"

    private fun DocumentReference.getTotalItemsCount(doOnSuccessBlock: (Int) -> Unit) {
        this.get().addOnCompleteListener {
            if (it.result?.data != null) { // Fixes
                it.result?.getField<Int>(Field.TOTAL_ITEMS)?.let { items -> doOnSuccessBlock(items) }
            } else {
                doOnSuccessBlock(0)
            }
        }
    }

    private fun DocumentReference.updateTotalItemsCounter(by: Long, doOnSuccess: (() -> Unit)? = null) {
        this.set(hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)), SetOptions.merge()).addOnCompleteListener {
            doOnSuccess?.invoke()
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "MovieInfoFragment"
    }
}
