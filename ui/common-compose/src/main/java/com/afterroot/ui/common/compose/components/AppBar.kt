/*
 * 2021 AfterROOT
 */
package com.afterroot.ui.common.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.theme.appBarTitleStyle

/**
 * Extended Version of [TopAppBar] with Center Title.
 *
 * @param navigationIcon Should be [IconButton]
 * @param actions Should be [IconButton]
 */
@Composable
fun CommonAppBar(
    modifier: Modifier = Modifier,
    withTitle: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (navigationIcon != null) {
                Row(
                    Modifier
                        .fillMaxHeight()
                        .width(68.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = navigationIcon
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center)
            ) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                    content = { Text(text = withTitle, style = appBarTitleStyle) }
                )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}

@Composable
fun UpActionButton(onUpClick: () -> Unit) {
    IconButton(onClick = onUpClick) {
        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
    }
}

val AppBarHeight = 56.dp
