/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbNetworkId
import app.moviebase.tmdb.model.TmdbWatchProviderId
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.DiscoverCategory
import javax.inject.Inject
import app.moviebase.tmdb.discover.DiscoverCategory as TmdbDiscoverCategory

class TmdbDiscoverCategoryToDiscoverCategory @Inject constructor() :
  Mapper<TmdbDiscoverCategory, DiscoverCategory> {
  override fun map(from: TmdbDiscoverCategory): DiscoverCategory {
    return when (from) {
      TmdbDiscoverCategory.AiringToday -> DiscoverCategory.AIRING_TODAY
      TmdbDiscoverCategory.NowPlaying -> DiscoverCategory.NOW_PLAYING
      TmdbDiscoverCategory.OnTv -> DiscoverCategory.ON_TV
      TmdbDiscoverCategory.Upcoming -> DiscoverCategory.UPCOMING
      is TmdbDiscoverCategory.OnDvd -> DiscoverCategory.ON_DVD
      is TmdbDiscoverCategory.Popular -> DiscoverCategory.POPULAR
      is TmdbDiscoverCategory.TopRated -> DiscoverCategory.TOP_RATED
      is TmdbDiscoverCategory.Network -> {
        when (from.network) {
          TmdbNetworkId.NETFLIX -> DiscoverCategory.ON_NETFLIX
          TmdbNetworkId.AMAZON -> DiscoverCategory.ON_AMAZON
          TmdbNetworkId.DISNEY_PLUS -> DiscoverCategory.ON_DISNEY_PLUS
          TmdbNetworkId.APPLE_TV -> DiscoverCategory.ON_APPLE_TV
          else -> DiscoverCategory.UNCATEGORIZED
        }
      }

      is TmdbDiscoverCategory.OnStreaming -> {
        return if (from.watchProviders.items.contains(TmdbWatchProviderId.Flatrate.NETFLIX)) {
          DiscoverCategory.ON_NETFLIX
        } else if (from.watchProviders.items.contains(
            TmdbWatchProviderId.Flatrate.AMAZON_PRIME_VIDEO_TIER_A,
          )
        ) {
          DiscoverCategory.ON_AMAZON
        } else if (from.watchProviders.items.contains(
            TmdbWatchProviderId.Flatrate.AMAZON_PRIME_VIDEO_TIER_B,
          )
        ) {
          DiscoverCategory.ON_AMAZON
        } else if (from.watchProviders.items.contains(TmdbWatchProviderId.Flatrate.DISNEY_PLUS)) {
          DiscoverCategory.ON_DISNEY_PLUS
        } else if (from.watchProviders.items.contains(TmdbWatchProviderId.Flatrate.APPLE_TV_PLUS)) {
          DiscoverCategory.ON_APPLE_TV
        } else {
          DiscoverCategory.UNCATEGORIZED
        }
      }
    }
  }
}
