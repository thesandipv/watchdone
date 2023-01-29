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
package com.afterroot.watchdone.ui.media

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.tivi.common.compose.Layout
import app.tivi.common.compose.ui.copy
import app.tivi.common.compose.ui.plus
import com.afterroot.data.utils.valueOrBlank
import com.afterroot.ui.common.compose.components.BasePosterCard
import com.afterroot.ui.common.compose.components.SuggestionChipGroup
import com.afterroot.ui.common.compose.theme.PreviewTheme
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.mapper.toDBMedia
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.viewmodel.MediaInfoViewModel
import com.afterroot.watchdone.viewmodel.SelectedMedia

@Composable
fun OverviewContent(viewModel: MediaInfoViewModel) {
    val selectedMedia by viewModel.selectedMedia.collectAsState()

    if (selectedMedia is SelectedMedia.Movie) {
        OverviewContent(movie = (selectedMedia as SelectedMedia.Movie).data)
    } else if (selectedMedia is SelectedMedia.TV) {
        OverviewContent(tv = (selectedMedia as SelectedMedia.TV).data)
    }
}

@Composable
fun OverviewContent(
    selectedMedia: SelectedMedia,
    onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit,
    onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedMedia is SelectedMedia.Movie) {
        OverviewContent(
            modifier = modifier,
            movie = selectedMedia.data,
            onWatchlistAction = onWatchlistAction,
            onWatchedAction = onWatchedAction
        )
    } else if (selectedMedia is SelectedMedia.TV) {
        OverviewContent(
            modifier = modifier,
            tv = selectedMedia.data,
            onWatchlistAction = onWatchlistAction,
            onWatchedAction = onWatchedAction
        )
    }
}

@Composable
fun OverviewContent(
    modifier: Modifier = Modifier,
    movie: Movie? = null,
    tv: TV? = null,
    isInWatchlist: Boolean = false,
    isWatched: Boolean = false,
    onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
    onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> }
) {
    val gutter = Layout.gutter
    val bodyMargin = Layout.bodyMargin

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(vertical = gutter)) {
            BasePosterCard(
                title = movie?.title ?: tv?.name, posterPath = movie?.posterPath ?: tv?.posterPath, modifier = Modifier
                    .padding(start = bodyMargin)
                    .height(192.dp)
                    .aspectRatio(2 / 3f)

            )
            Column {
                // TODO Extract composable
                Row(
                    modifier = Modifier.padding(horizontal = bodyMargin),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Row(
                    modifier = Modifier.padding(horizontal = bodyMargin),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Event,
                        contentDescription = "Release Date",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.padding(2.dp))

                    ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
                        Text(text = (movie?.releaseDate ?: tv?.releaseDate).valueOrBlank(), fontSize = 12.sp)
                    }
                }

                SuggestionChipGroup(
                    chipSpacing = 8.dp,
                    horizontalPadding = bodyMargin,
                    modifier = Modifier
                        .padding(top = gutter / 2),
                    list = movie?.genres?.map {
                        it.name
                    } ?: tv?.genres?.map {
                        it.name
                    } ?: emptyList()
                )
            }
        }

        ProvideTextStyle(value = ubuntuTypography.labelMedium) {
            WatchlistActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = bodyMargin, vertical = gutter),
                isInWatchlist = isInWatchlist,
                isWatched = isWatched,
                onWatchlistAction = { onWatchlistAction(it, movie?.toDBMedia() ?: tv?.toDBMedia() ?: DBMedia.Empty) },
                onWatchedAction = { onWatchedAction(it, movie?.toDBMedia() ?: tv?.toDBMedia() ?: DBMedia.Empty) }
            )
        }

        OverviewText(
            text = (movie?.overview ?: tv?.overview) ?: "",
            modifier = Modifier.padding(horizontal = bodyMargin, vertical = gutter)
        )
    }
}

@Composable
fun OverviewText(text: String, modifier: Modifier = Modifier) {
    var maxLines by remember { mutableStateOf(4) }
    ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
        Text(
            text = text,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
            modifier = Modifier
                .animateContentSize(animationSpec = tween(100))
                .clickable { maxLines = if (maxLines == 4) Int.MAX_VALUE else 4 }
                .then(modifier)
        )
    }
}

@Composable
fun WatchlistActions(
    modifier: Modifier = Modifier,
    isInWatchlist: Boolean = false,
    isWatched: Boolean = false,
    onWatchlistAction: (checked: Boolean) -> Unit,
    onWatchedAction: (checked: Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TwoStateButton(
            modifier = Modifier.weight(1f),
            checked = isInWatchlist,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                .copy(copyTop = false, copyBottom = false)
                .plus(PaddingValues(vertical = 4.dp)),
            checkedText = "Remove from Watchlist",
            uncheckedText = "Add to Watchlist",
            checkedIcon = Icons.Filled.Bookmark,
            uncheckedIcon = Icons.Outlined.Bookmark,
            onClick = onWatchlistAction
        )

        TwoStateOutlinedButton(
            modifier = Modifier.weight(1f),
            checked = isWatched,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                .copy(copyTop = false, copyBottom = false)
                .plus(PaddingValues(vertical = 4.dp)),
            checkedText = "Mark as Unwatched",
            uncheckedText = "Mark as Watched",
            checkedIcon = Icons.Filled.Clear,
            uncheckedIcon = Icons.Filled.Done,
            onClick = onWatchedAction
        )
    }
}

@Composable
fun TwoStateButton(
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ButtonWithIconContentPadding,
    checkedText: String,
    uncheckedText: String,
    checkedIcon: ImageVector,
    uncheckedIcon: ImageVector,
    onClick: (checked: Boolean) -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            onClick(!checked)
        },
        contentPadding = contentPadding
    ) {
        Icon(
            if (checked) checkedIcon else uncheckedIcon,
            contentDescription = checkedText,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = if (checked) checkedText else uncheckedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
fun TwoStateOutlinedButton(
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ButtonWithIconContentPadding,
    checkedText: String,
    uncheckedText: String,
    checkedIcon: ImageVector,
    uncheckedIcon: ImageVector,
    onClick: (checked: Boolean) -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = {
            onClick(!checked)
        },
        contentPadding = contentPadding
    ) {
        Icon(
            if (checked) checkedIcon else uncheckedIcon,
            contentDescription = checkedText,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = if (checked) checkedText else uncheckedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.animateContentSize()
        )
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
