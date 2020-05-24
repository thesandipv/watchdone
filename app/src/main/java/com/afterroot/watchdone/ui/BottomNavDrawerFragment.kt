/*
 * Copyright (C) 2020 Sandip Vaghela
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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.ui.home.HomeViewModel
import com.afterroot.watchdone.utils.getGravtarUrl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_bottom.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get

class BottomNavDrawerFragment : BottomSheetDialogFragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user = get<FirebaseAuth>().currentUser
        navigation_view.apply {
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_settings -> {
                        findNavController().navigate(R.id.toSettings)
                        dismiss()
                    }
                    R.id.tmdb_login -> {
                        val dialog = MaterialDialog(requireContext()).show {
                            customView(R.layout.loading_dialog)
                        }
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
                }
                true
            }
            getHeaderView(0).apply {
                nav_header_name.text = user?.displayName
                nav_header_email.text = user?.email
                button_sign_out.setOnClickListener {
                    get<FirebaseAuth>().signOut()
                }
                GlideApp.with(requireContext()).load(getGravtarUrl(user?.email.toString())).circleCrop().into(avatar)
                setOnClickListener {
                    findNavController().navigate(R.id.toEditProfile)
                    dismiss()
                }
            }
        }
    }
}