/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.databinding.FragmentEditProfileBinding
import com.afterroot.watchdone.ui.SplashActivity
import com.afterroot.watchdone.utils.getGravatarUrl
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Inject lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser

    @Inject lateinit var firebaseUtils: FirebaseUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUtils.isUserSignedIn) {
            user = firebaseUtils.firebaseUser!!
            binding.inputProfileName.setText(user.displayName)
            binding.inputEmail.setText(user.email)
            binding.inputEmail.isEnabled = false
            requireActivity().apply {
/*
                findViewById<FloatingActionButton>(R.id.fab).apply {
                    setOnClickListener {
                        val newName = binding.inputProfileName.text.toString().trim()
                        if (user.displayName != newName) {
                            val request = UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build()
                            user.updateProfile(request).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    requireContext().toast(getString(CommonR.string.msg_profile_updated))
                                    db.collection(Collection.USERS)
                                        .document(user.uid)
                                        .update(Field.NAME, newName)
                                }
                            }
                        } else {
                            requireContext().toast(getString(CommonR.string.msg_no_changes))
                        }
                    }
                    setImageDrawable(
                        requireContext().getTintedDrawable(
                            CommonR.drawable.ic_save,
                            requireContext().getMaterialColor(MaterialR.attr.colorOnSecondary)
                        )
                    )
                }
*/
                Glide.with(
                    requireContext(),
                ).load(getGravatarUrl(user.email.toString())).circleCrop().into(binding.avatar)
            }
        } else {
            startActivity(Intent(this.context, SplashActivity::class.java))
        }
    }
}
