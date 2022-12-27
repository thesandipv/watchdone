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

import androidx.compose.runtime.Immutable
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.SelectedMedia
import info.movito.themoviedbapi.model.Multi

@Immutable
data class MediaInfoViewState(
    val mediaType: Multi.MediaType? = Multi.MediaType.MOVIE,
    val selectedMedia: SelectedMedia? = null,
    val seasonInfo: State<Season> = State.loading(),
    val selectedSeason: Int = 1,
    val isLoading: Boolean = false,
    val refresh: Boolean = false,
    val empty: Boolean = true
) : ViewState() {
    companion object {
        val Empty = MediaInfoViewState()
    }
}
