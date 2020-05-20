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
import androidx.navigation.fragment.findNavController
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_bottom.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.apache.commons.codec.digest.DigestUtils
import org.koin.android.ext.android.get
import java.util.Locale

class BottomNavDrawerFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user = get<FirebaseAuth>().currentUser
        navigation_view.apply {
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_settings -> findNavController().navigate(R.id.navigation_settings)
                }
                dismiss()
                true
            }
            getHeaderView(0).apply {
                nav_header_name.text = user?.displayName
                nav_header_email.text = user?.email
                button_sign_out.setOnClickListener {
                    get<FirebaseAuth>().signOut()
                }
                val emailMd5 = DigestUtils.md5Hex(user?.email?.toLowerCase(Locale.getDefault()))
                GlideApp.with(requireContext()).load("https://www.gravatar.com/avatar/$emailMd5").circleCrop().into(avatar)
            }
        }
    }
}