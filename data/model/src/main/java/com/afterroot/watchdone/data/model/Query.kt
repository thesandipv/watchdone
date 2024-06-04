/*
 * Copyright (C) 2020-2022 AfterROOT
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

package com.afterroot.watchdone.data.model

class Query {
  val params: MutableMap<String, String> = hashMapOf()

  fun page(page: Int?): Query {
    if (page != null && page > 0) {
      params[ParamNames.PAGE] = page.toString()
    }
    return this
  }

  fun language(language: String): Query {
    if (language.isNotBlank()) {
      params[ParamNames.LANGUAGE] = language
    }
    return this
  }

  fun query(query: String): Query {
    if (query.isNotBlank()) {
      params[ParamNames.QUERY] = query
    }
    return this
  }

  fun getQuery() = params[ParamNames.QUERY] ?: ""

  private val searchParams = listOf(
    ParamNames.LANGUAGE,
    ParamNames.QUERY,
    ParamNames.PAGE,
    ParamNames.ADULT,
    ParamNames.REGION,
    ParamNames.YEAR,
    ParamNames.PRIMARY_RELEASE_YEAR,
  )

  fun forSearch(): Query {
    params.filterKeys {
      searchParams.contains(it)
    }
    return this
  }

  fun validateForSearch(): Boolean {
    return params[ParamNames.QUERY]?.isNotBlank() ?: false
  }

  object ParamNames {
    const val ADULT = "include_adult"
    const val LANGUAGE = "language"
    const val PAGE = "page"
    const val PRIMARY_RELEASE_YEAR = "primary_release_year"
    const val QUERY = "query"
    const val REGION = "region"
    const val YEAR = "year"
  }

  override fun toString(): String {
    return params.toString()
  }
}
