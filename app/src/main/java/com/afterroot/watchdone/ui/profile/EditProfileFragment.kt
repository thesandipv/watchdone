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
package com.afterroot.watchdone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.databinding.FragmentEditProfileBinding
import com.afterroot.watchdone.ui.SplashActivity
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.getGravatarUrl
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val db: FirebaseFirestore by inject()
    private lateinit var user: FirebaseUser
    private val firebaseUtils: FirebaseUtils by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUtils.isUserSignedIn) {
            user = firebaseUtils.firebaseUser!!
            binding.inputProfileName.setText(user.displayName)
            binding.inputEmail.setText(user.email)
            binding.inputEmail.isEnabled = false
            requireActivity().apply {
                findViewById<FloatingActionButton>(R.id.fab).apply {
                    setOnClickListener {
                        val newName = binding.inputProfileName.text.toString().trim()
                        if (user.displayName != newName) {
                            val request = UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build()
                            user.updateProfile(request).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    requireContext().toast(getString(R.string.msg_profile_updated))
                                    db.collection(Collection.USERS)
                                        .document(user.uid)
                                        .update(Field.NAME, newName)
                                }
                            }
                        } else requireContext().toast(getString(R.string.msg_no_changes))
                    }
                    setImageDrawable(requireContext().getDrawableExt(R.drawable.ic_save, R.color.color_on_secondary))
                }
                findViewById<BottomAppBar>(R.id.toolbar).apply {
                    fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                }
                GlideApp.with(requireContext()).load(getGravatarUrl(user.email.toString())).circleCrop().into(binding.avatar)
            }
        } else {
            startActivity(Intent(this.context, SplashActivity::class.java))
        }
    }
}
