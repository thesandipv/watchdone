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
package com.afterroot.ui.common.compose.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.afterroot.ui.common.compose.components.LocalSettings
import com.afterroot.utils.getMaterialColor
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.resources.R as CommonR
import com.google.android.material.R as MaterialR

@Composable
fun Theme(context: Context, settings: Settings, content: @Composable () -> Unit) {
    val background = if (isSystemInDarkTheme()) {
        Color(ContextCompat.getColor(context, CommonR.color.md_theme_dark_background))
    } else {
        Color(ContextCompat.getColor(context, CommonR.color.md_theme_light_background))
    }

    val contextColorScheme by lazy {
        ColorScheme(
            primary = Color(context.getMaterialColor(MaterialR.attr.colorPrimary)),
            onPrimary = Color(context.getMaterialColor(MaterialR.attr.colorOnPrimary)),
            primaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorPrimaryContainer),
            ),
            onPrimaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorOnPrimaryContainer),
            ),
            inversePrimary = Color(context.getMaterialColor(MaterialR.attr.colorPrimaryInverse)),
            secondary = Color(context.getMaterialColor(MaterialR.attr.colorSecondary)),
            onSecondary = Color(context.getMaterialColor(MaterialR.attr.colorOnSecondary)),
            secondaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorSecondaryContainer),
            ),
            onSecondaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorOnSecondaryContainer),
            ),
            tertiary = Color(context.getMaterialColor(MaterialR.attr.colorTertiary)),
            onTertiary = Color(context.getMaterialColor(MaterialR.attr.colorOnTertiary)),
            tertiaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorTertiaryContainer),
            ),
            onTertiaryContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorOnTertiaryContainer),
            ),
            background = background,
            onBackground = Color(context.getMaterialColor(MaterialR.attr.colorOnBackground)),
            surface = Color(context.getMaterialColor(MaterialR.attr.colorSurface)),
            onSurface = Color(context.getMaterialColor(MaterialR.attr.colorOnSurface)),
            surfaceVariant = Color(context.getMaterialColor(MaterialR.attr.colorSurfaceVariant)),
            onSurfaceVariant = Color(
                context.getMaterialColor(MaterialR.attr.colorOnSurfaceVariant),
            ),
            inverseSurface = Color(context.getMaterialColor(MaterialR.attr.colorSurfaceInverse)),
            inverseOnSurface = Color(
                context.getMaterialColor(MaterialR.attr.colorOnSurfaceInverse),
            ),
            error = Color(context.getMaterialColor(MaterialR.attr.colorError)),
            onError = Color(context.getMaterialColor(MaterialR.attr.colorOnError)),
            errorContainer = Color(context.getMaterialColor(MaterialR.attr.colorErrorContainer)),
            onErrorContainer = Color(
                context.getMaterialColor(MaterialR.attr.colorOnErrorContainer),
            ),
            outline = Color(context.getMaterialColor(MaterialR.attr.colorOutline)),
            outlineVariant = Color(context.getMaterialColor(MaterialR.attr.colorOutlineVariant)),
            surfaceTint = Color(context.getMaterialColor(MaterialR.attr.colorPrimary)),
            scrim = Color(red = 0, green = 0, blue = 0),
        )
    }

    val colorScheme: ColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        contextColorScheme
    }

    val lightColorScheme: ColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        contextColorScheme
    }

    val finalColorScheme = if (isSystemInDarkTheme()) colorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = finalColorScheme,
        typography = ubuntuTypography,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides contentColorFor(backgroundColor = finalColorScheme.background),
                LocalSettings provides settings,
                content = content,
            )
        },
    )
}

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = ubuntuTypography,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides contentColorFor(backgroundColor = darkColorScheme().background),
                content = content,
            )
        },
    )
}
