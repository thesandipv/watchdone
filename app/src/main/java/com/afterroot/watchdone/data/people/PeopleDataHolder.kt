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

package com.afterroot.watchdone.data.people

import com.afterroot.tmdbapi.TmdbPeople
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.watchdone.data.base.DataHolder

data class PeopleDataHolder(
    override var data: Person,
    override var additionalParams: PeopleAdditionalParams? = null
) : DataHolder<Person, PeopleAdditionalParams>()

fun TmdbPeople.PersonResultsPage.toPeopleDataHolder(): List<PeopleDataHolder> {
    val list = mutableListOf<PeopleDataHolder>()
    this.results.forEach {
        if (it != null) {
            val holder = PeopleDataHolder(it)
            list.add(holder)
        }
    }
    return list
}