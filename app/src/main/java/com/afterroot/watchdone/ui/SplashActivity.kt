/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.R
import com.afterroot.watchdone.ui.common.showNetworkDialog
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.browse
import javax.inject.Inject
import com.afterroot.watchdone.resources.R as CommonR
import com.google.android.material.R as MaterialR

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val _tag = "SplashActivity"
    private val networkViewModel: NetworkViewModel by viewModels()

    @Inject lateinit var firebaseAuth: FirebaseAuth
    // @Inject lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = getString(CommonR.string.theme_device_default)
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                getString(CommonR.string.theme_device_default) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                getString(CommonR.string.theme_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                getString(CommonR.string.theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
                getString(CommonR.string.theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setUpNetworkObserver()
        when {
            firebaseAuth.currentUser == null -> {
                tryLogin()
            }
            intent.extras != null -> {
                intent.extras?.let {
                    val link = it.getString("link")
                    when {
                        link != null -> {
                            browse(link, true)
                            finish()
                        }
                        else -> {
                            launchMain()
                        }
                    }
                }
            }
            else -> {
                launchMain()
            }
        }
    }

    private fun tryLogin() {
        val pickerLayout = AuthMethodPickerLayout.Builder(R.layout.layout_main_auth)
            .setGoogleButtonId(R.id.button_auth_sign_in_google)
            .setEmailButtonId(R.id.button_auth_sign_in_email)
            .setTosAndPrivacyPolicyId(R.id.text_top_pp)
            .build()

        resultLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(pickerLayout)
                .setTheme(MaterialR.style.Theme_MaterialComponents_DayNight_NoActionBar)
                .setLogo(CommonR.drawable.launch_icon)
                .setTosAndPrivacyPolicyUrls(getString(CommonR.string.url_tos), getString(CommonR.string.url_privacy_policy))
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build(),
                        AuthUI.IdpConfig.GoogleBuilder()
                            .setSignInOptions(
                                GoogleSignInOptions.Builder().requestProfile().requestEmail().requestId().build()
                            )
                            .build()
                    )
                ).build()
        )
    }

    private val resultLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        if (it.resultCode == Activity.RESULT_OK) {
            launchMain()
        } else {
            Toast.makeText(this, getString(CommonR.string.msg_login_failed), Toast.LENGTH_SHORT).show()
            tryLogin()
        }
    }

    private fun launchMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private var dialog: AlertDialog? = null
    private fun setUpNetworkObserver() {
        networkViewModel.monitor(
            this,
            onConnect = {
                if (dialog != null && dialog?.isShowing!!) dialog?.dismiss()
            },
            onDisconnect = {
                dialog = showNetworkDialog(state = it, positive = { setUpNetworkObserver() }, negative = { finish() })
            }
        )
    }
}
