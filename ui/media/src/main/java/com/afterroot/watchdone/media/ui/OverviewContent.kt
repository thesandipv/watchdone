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
package com.afterroot.watchdone.media.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afterroot.ui.common.compose.theme.PreviewTheme
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.resources.R

@Composable
fun OverviewContent(movie: Movie? = null, tv: TV? = null) {
    Box(modifier = Modifier.padding(16.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.padding(2.dp))

                ProvideTextStyle(value = ubuntuTypography.bodyLarge) {
                    Text(
                        text = stringResource(
                            id = R.string.media_info_rating_text,
                            movie?.voteAverage ?: tv?.voteAverage ?: 0.0
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Event,
                    contentDescription = "Release Date",
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.padding(2.dp))

                ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
                    Text(text = (movie?.releaseDate ?: tv?.releaseDate) ?: "", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            var maxLines by remember { mutableStateOf(4) }

            ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
                Text(
                    text = (movie?.overview ?: tv?.overview) ?: "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = maxLines,
                    modifier = Modifier
                        .animateContentSize(animationSpec = tween(100))
                        .clickable {
                            maxLines = if (maxLines == 4) {
                                Int.MAX_VALUE
                            } else {
                                4
                            }
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverviewContent() {
    PreviewTheme {
        val movie = Movie(
            voteAverage = 7.2,
            overview = "After his retirement is interrupted by Gorr the God Butcher, a galactic killer who seeks the extinction of the gods, Thor Odinson enlists the help of King Valkyrie, Korg, and ex-girlfriend Jane Foster, who now wields Mjolnir as the Mighty Thor. Together they embark upon a harrowing cosmic adventure to uncover the mystery of the God Butcher’s vengeance and stop him before it’s too late.",
            releaseDate = "21/08/22"
        )
        OverviewContent(movie = movie)
    }
}
