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
package com.afterroot.watchdone.ui.movie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.palette.graphics.Palette
import androidx.transition.AutoTransition
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.core.extensions.showStaticProgressDialog
import com.afterroot.core.extensions.updateProgressText
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi2.model.MovieAppendableResponses
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.CastListAdapter
import com.afterroot.watchdone.adapter.SearchMoviesListAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toDbMovie
import com.afterroot.watchdone.data.mapper.toMovie
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.databinding.FragmentMovieInfoBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.utils.createPosterUrl
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.afterroot.watchdone.view.SectionalListView
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.jetbrains.anko.browse
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.email
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.qualifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MovieInfoFragment : Fragment() {
    private lateinit var binding: FragmentMovieInfoBinding
    private lateinit var watchlistItemReference: CollectionReference
    private lateinit var watchListRef: DocumentReference
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val moviesRepository: MoviesRepository by inject()
    private val firebaseUtils: FirebaseUtils by inject()
    private val myDatabase: MyDatabase by inject()
    private val settings: Settings by inject()
    private var adLoaded: Boolean = false
    private var clickedAddWl: Boolean = false
    private var menu: Menu? = null
    private var progressDialog: MaterialDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentMovieInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argMovieId = arguments?.getString("movieId")
        if (argMovieId != null) {
            launchShowingProgress {
                homeViewModel.selectMovie(getInfoFromServerForCompare(argMovieId.toInt()))
            }
        }

        homeViewModel.getWatchlistSnapshot(firebaseUtils.uid!!)
            .observe(
                viewLifecycleOwner,
                { state: ViewModelState? ->
                    if (state is ViewModelState.Loaded<*>) {
                        homeViewModel.selectedMovie.observe(
                            viewLifecycleOwner,
                            { movie: Movie ->
                                updateUI(movie)
                                launchShowingProgress {
                                    updateCast(movie)
                                }
                            }
                        )
                    }
                }
            )

        setErrorObserver()

        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun updateUI(movie: Movie) { // Do operations related to database
        watchListRef = get<FirebaseFirestore>().collectionWatchdone(
            id = get<FirebaseAuth>().currentUser?.uid.toString(),
            isUseOnlyProdDB = settings.isUseProdDb
        ).document(Collection.WATCHLIST)
        watchlistItemReference = watchListRef.collection(Collection.ITEMS)
        binding.apply {
            settings = this@MovieInfoFragment.settings
            this.movie = movie
            updateGenres(movie)
            posterUrl = movie.posterPath?.let { this@MovieInfoFragment.settings.createPosterUrl(it) }
            // executePendingBindings()
            watchlistItemReference.whereEqualTo(Field.ID, movie.id).get(Source.CACHE).addOnSuccessListener {
                kotlin.runCatching { // Fix crash if user quickly press back button just after navigation
                    val isInWatchlist = it.documents.size > 0
                    var isWatched = false
                    var resultFromDB: Movie? = null
                    var selectedMovieDocId: String? = null
                    if (isInWatchlist) {
                        val document = it.documents[0]
                        document.getBoolean(Field.IS_WATCHED)?.let { watched ->
                            isWatched = watched
                        }
                        resultFromDB = document.toObject(Movie::class.java)
                        selectedMovieDocId = document.id
                        launchShowingProgress { // Only for compare and update firestore
                            val resultFromServer = getInfoFromServerForCompare(movie.id)
                            if (resultFromServer != resultFromDB) {
                                val selectedMovieDocRef = watchlistItemReference.document(selectedMovieDocId)
                                selectedMovieDocRef.set(resultFromServer, SetOptions.merge()).addOnSuccessListener {
                                    hideProgress()
                                }
                                this.movie = resultFromServer
                                updateGenres(resultFromServer)
                            } else {
                                hideProgress()
                            }
                        }
                    } else {
                        launchShowingProgress {
                            this.movie = getInfoFromServer(movie.id)
                            updateGenres(this.movie!!)
                            hideProgress()
                        }
                    }
                    actionAddWlist.apply {
                        visible(true)
                        if (!isInWatchlist) {
                            text = getString(R.string.text_add_to_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark_border)
                            setOnClickListener {
                                doShowingProgress {
                                    progressDialog = requireContext().showStaticProgressDialog("Please Wait...")
                                    watchListRef.getTotalItemsCount { itemsCount ->
                                        if (itemsCount < 5) {
                                            addToWatchlist(movie)
                                        } else {
                                            snackBarMessage(requireContext().getString(R.string.msg_limit_error))
                                            hideProgress()
                                            MaterialDialog(requireContext()).show {
                                                title(R.string.text_add_to_watchlist)
                                                message(R.string.dialog_msg_rewarded_ad)
                                                positiveButton(R.string.text_ok) {
                                                    clickedAddWl = true
                                                    createAndLoadRewardedAd()
                                                    progressDialog =
                                                        requireContext().showStaticProgressDialog("Please Wait...Ad is Loading")

                                                }
                                                negativeButton(R.string.fui_cancel)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            text = getString(R.string.text_remove_from_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark)
                            setOnClickListener {
                                selectedMovieDocId?.let { id -> watchlistItemReference.document(id).delete() }
                                watchListRef.updateTotalItemsCounter(-1)
                                snackBarMessage(requireContext().getString(R.string.msg_removed_from_wl))
                            }
                        }
                    }
                    actionMarkWatched.apply {
                        visible(true)
                        if (isInWatchlist && selectedMovieDocId != null && resultFromDB != null) {
                            if (isWatched) {
                                text = getString(R.string.text_mark_as_unwatched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_clear)
                            } else {
                                text = getString(R.string.text_mark_as_watched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_done)
                            }
                            setOnClickListener {
                                watchlistItemReference.document(selectedMovieDocId)
                                    .update(Field.IS_WATCHED, !isWatched)
                            }
                        } else {
                            setOnClickListener {
                                snackBarMessage(requireContext().getString(R.string.msg_add_to_wl_first))
                            }
                        }
                    }

                    composeView.setContent {
                        Column {
                            SimilarMovies(movie)
                        }

                    }
                }
            }
            menu?.findItem(R.id.action_view_imdb)?.isVisible = !binding.movie?.imdbId.isNullOrBlank()
            loadNewAd()
        }
    }

    @Composable
    fun SimilarMovies(movie: Movie) {
        val state = remember { mutableStateOf(emptyList<Movie>()) }
        val moviesListAdapter = SearchMoviesListAdapter(movieItemSelectedCallback)

        AndroidView(
            factory = {
                SectionalListView(it).withTitle("Similar Movies").withLoading().apply {
                    setAdapter(moviesListAdapter)
                    lifecycleScope.launch {
                        state.value = moviesRepository.getSimilar(movie.id).toMovies()
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .clipToBounds(),
            update = {
                it.isLoaded = true
                moviesListAdapter.submitList(state.value)
            }
        )
    }

    private val movieItemSelectedCallback = object : ItemSelectedCallback<Movie> {
        override fun onClick(position: Int, view: View?, item: Movie) {
            super.onClick(position, view, item)
            //homeViewModel.selectMovie(item)
            view?.findNavController()?.navigate(R.id.movieInfoToMovieInfo)
        }
    }

    private fun addToWatchlist(movie: Movie) {
        watchlistItemReference.add(movie.toDbMovie())
        watchListRef.updateTotalItemsCounter(1)
        snackBarMessage(requireContext().getString(R.string.msg_added_to_wl))
        hideProgress()
    }

    @SuppressLint("MissingPermission")
    private fun createAndLoadRewardedAd() {
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                super.onAdLoaded(ad)
                adLoaded = true
                if (clickedAddWl) {
                    hideProgress()
                    showAd(ad)
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                adLoaded = false
                if (clickedAddWl) {
                    progressDialog?.updateProgressText("Adding ${binding.movie?.title} to Watchlist")
                    addAfterWatchingAd()
                }
            }
        }
        RewardedAd.load(
            requireActivity(),
            getString(R.string.ad_rewarded_unit_id),
            AdManagerAdRequest.Builder().build(),
            adLoadCallback
        )
    }

    private fun loadNewAd() {
        createAndLoadRewardedAd()
    }

    fun addAfterWatchingAd() {
        doShowingProgress {
            watchListRef.updateTotalItemsCounter(-5) {
                binding.movie?.let { addToWatchlist(it) }
            }
        }
    }

    private fun showAd(rewardedAd: RewardedAd) {
/*
        fun onRewardedAdClosed() {
            adLoaded = false
            loadNewAd()
        }
*/
        rewardedAd.show(requireActivity()) {
            clickedAddWl = false
            addAfterWatchingAd()
        }
    }

    private suspend fun getInfoFromServer(id: Int) = withContext(Dispatchers.IO) {
        moviesRepository.getFullMovieInfo(id, MovieAppendableResponses.credits).toMovie()
    }

    private suspend fun getInfoFromServerForCompare(id: Int) = withContext(Dispatchers.IO) {
        moviesRepository.getMovieInfo(id).toMovie()
    }

    private fun DocumentReference.updateTotalItemsCounter(by: Long, doOnSuccess: (() -> Unit)? = null) {
        this.set(hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)), SetOptions.merge()).addOnCompleteListener {
            doOnSuccess?.invoke()
        }
    }

    private fun DocumentReference.getTotalItemsCount(doOnSuccess: (Int) -> Unit) {
        this.get().addOnCompleteListener {
            if (it.result?.data != null) { // Fixes
                it.result?.getField<Int>(Field.TOTAL_ITEMS)?.let { items -> doOnSuccess(items) }
            } else {
                doOnSuccess(0)
            }
        }
    }

    private fun updateGenres(movie: Movie) {
        if (movie.genres == null) {
            movie.genreIds?.let {
                myDatabase.genreDao().getGenres(it).observe(
                    viewLifecycleOwner,
                    { roomGenres ->
                        binding.genres = roomGenres
                    }
                )
            }
        } else {
            binding.genres = movie.genres
        }
    }

    private suspend fun updateCast(movie: Movie) {
        val credits = get<MoviesRepository>().getCredits(movie.id)
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

    private fun setErrorObserver() {
        homeViewModel.error.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    hideProgress()
                    snackBarMessage("Error: $it")
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_imdb -> {
                binding.movie?.imdbId?.let {
                    val imdbUrl = HttpUrl.Builder().scheme("https")
                        .host("www.imdb.com")
                        .addPathSegments("title").addPathSegment(it).build()
                    requireContext().browse(imdbUrl.toUrl().toString(), true)
                }
            }
            R.id.send_feedback -> {
                requireContext().email(
                    email = get(qualifier("feedback_email")),
                    subject = "Watchdone Feedback",
                    text = getMailBodyForFeedback(get())
                )
            }
            R.id.action_share_to_ig_story -> {
                val dialog = requireContext().showStaticProgressDialog(getString(R.string.text_please_wait))
                GlideApp.with(requireContext()).asBitmap()
                    .load(settings.baseUrl + Constants.IG_SHARE_IMAGE_SIZE + binding.movie?.posterPath)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            var fos: FileOutputStream? = null
                            val file = File(
                                requireContext().externalCacheDir.toString(),
                                "${binding.movie?.id}.jpg"
                            )
                            try {
                                fos = FileOutputStream(file)
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                fos?.flush()
                                fos?.close()
                                val uri = FileProvider.getUriForFile(
                                    requireContext(),
                                    "${BuildConfig.APPLICATION_ID}.provider",
                                    file
                                )
                                Palette.from(resource).generate { palette ->
                                    val intent = Intent(Constants.IG_SHARE_ACTION).apply {
                                        type = Constants.MIME_TYPE_JPEG
                                        putExtra(Constants.IG_EXTRA_INT_ASSET_URI, uri)
                                        putExtra(Constants.IG_EXTRA_SOURCE_APP, BuildConfig.APPLICATION_ID)
                                        putExtra(
                                            Constants.IG_EXTRA_CONTENT_URL,
                                            HttpUrl.Builder().scheme(Constants.SCHEME_HTTPS).host(Constants.WATCHDONE_HOST)
                                                .addPathSegment("movie").addPathSegment(binding.movie?.id.toString())
                                                .build().toString()

                                        )
                                        putExtra(
                                            Constants.IG_EXTRA_TOP_COLOR,
                                            palette?.getVibrantColor(
                                                palette.getMutedColor(
                                                    ContextCompat.getColor(requireContext(), R.color.color_primary)
                                                )
                                            )?.toHex()
                                        )
                                        putExtra(
                                            Constants.IG_EXTRA_BOTTOM_COLOR,
                                            palette?.getDarkVibrantColor(
                                                palette.getDarkMutedColor(
                                                    ContextCompat.getColor(requireContext(), R.color.color_primary_variant)
                                                )
                                            )?.toHex()
                                        )
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    requireActivity().grantUriPermission(
                                        Constants.IG_PACKAGE_NAME,
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )
                                    if (requireActivity().packageManager.resolveActivity(intent, 0) != null) {
                                        requireActivity().startActivity(intent)
                                        dialog.dismiss()
                                    }
                                }
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_info, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

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
        binding.root.snackbar(message).anchorView = requireActivity().findViewById(R.id.toolbar)
    }

    private fun Int.toHex() = "#${Integer.toHexString(this)}"

    companion object {
        @Suppress("unused")
        private const val TAG = "MovieInfoFragment"
    }
}
