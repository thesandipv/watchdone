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

package com.afterroot.watchdone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afterroot.core.extensions.showStaticProgressDialog
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.watchdone.R
import com.afterroot.watchdone.databinding.FragmentBottomBinding
import com.afterroot.watchdone.databinding.NavHeaderBinding
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

@Suppress("EXPERIMENTAL_API_USAGE")
class BottomNavDrawerFragment : BottomSheetDialogFragment(), KoinComponent {
    private lateinit var binding: FragmentBottomBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val auth: FirebaseAuth by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        binding.navigationView.apply {
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_settings -> {
                        findNavController().navigate(R.id.toSettings)
                        dismiss()
                    }
                    R.id.tmdb_login -> {
                        val dialog = requireContext().showStaticProgressDialog("Loading...")
                        homeViewModel.getResponseRequestToken().observe(viewLifecycleOwner, Observer { response ->
                            if (response.success) {
                                try {
                                    requireContext().browse(AuthRepository.getAuthVerifyUrl(response))
                                    dialog.dismiss()
                                    dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dialog.dismiss()
                                    dismiss()
                                }
                            } else {
                                requireContext().toast(response.statusMessage)
                                dialog.dismiss()
                                dismiss()
                            }
                        })
                    }
                    R.id.send_feedback -> {
                        requireContext().email(
                            email = "afterhasroot@gmail.com",
                            subject = "Watchdone Feedback",
                            text = getMailBodyForFeedback(get())
                        )
                    }
                    R.id.action_rate -> {
                        requireContext().browse(getString(R.string.url_play_store), true)
                    }
                }
                true
            }
            getHeaderView(0).apply {
                NavHeaderBinding.bind(this).apply {
                    this.user = user
                    // avatarUrl = getGravtarUrl(user?.email.toString())
                    buttonSignOut.setOnClickListener {
                        AuthUI.getInstance().signOut(requireContext())
                    }
                    root.setOnClickListener {
                        findNavController().navigate(R.id.toEditProfile)
                        dismiss()
                    }
                }

            }
        }
    }
}