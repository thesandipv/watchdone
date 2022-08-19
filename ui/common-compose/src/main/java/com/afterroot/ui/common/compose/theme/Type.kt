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
@file:OptIn(ExperimentalTextApi::class)

package com.afterroot.ui.common.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.afterroot.watchdone.resources.R

// Set of Material typography styles to start with
val typography = Typography()

val fontProvider by lazy {
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
}

val ubuntuFamily = googleFontFamily(
    "Ubuntu",
    listOf(
        FontWeight.Normal,
        FontWeight.Bold,
        FontWeight.ExtraLight,
        FontWeight.SemiBold
    )
)

val ubuntuTypography = Typography(
    displayLarge = typography.displayLarge.copy(fontFamily = ubuntuFamily),
    displayMedium = typography.displayMedium.copy(fontFamily = ubuntuFamily),
    displaySmall = typography.displaySmall.copy(fontFamily = ubuntuFamily),
    headlineLarge = typography.headlineLarge.copy(fontFamily = ubuntuFamily),
    headlineMedium = typography.headlineMedium.copy(fontFamily = ubuntuFamily),
    headlineSmall = typography.headlineSmall.copy(fontFamily = ubuntuFamily),
    titleLarge = typography.titleLarge.copy(fontFamily = ubuntuFamily),
    titleMedium = typography.titleMedium.copy(fontFamily = ubuntuFamily),
    titleSmall = typography.titleSmall.copy(fontFamily = ubuntuFamily),
    bodyLarge = typography.bodyLarge.copy(fontFamily = ubuntuFamily),
    bodyMedium = typography.bodyMedium.copy(fontFamily = ubuntuFamily),
    bodySmall = typography.bodySmall.copy(fontFamily = ubuntuFamily),
    labelLarge = typography.labelLarge.copy(fontFamily = ubuntuFamily),
    labelMedium = typography.labelMedium.copy(fontFamily = ubuntuFamily),
    labelSmall = typography.labelSmall.copy(fontFamily = ubuntuFamily),
)

fun googleFontFamily(
    name: String,
    weights: List<FontWeight>
): FontFamily {
    return FontFamily(
        weights.map {
            Font(GoogleFont(name), fontProvider, it)
        }
    )
}
