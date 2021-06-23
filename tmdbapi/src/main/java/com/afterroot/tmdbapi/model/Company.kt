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
package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.NamedIdElement
import com.fasterxml.jackson.annotation.JsonProperty

class Company : NamedIdElement() {
    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("headquarters")
    var headquarters: String? = null

    @JsonProperty("homepage")
    var homepage: String? = null

    @JsonProperty("logo_path")
    var logoPath: String? = null

    @JsonProperty("parent_company") // tbd is this field still supported? We need an example for info.movito.themoviedbapi.CompanyApiTest.testGetCompanyInfo
    var parentCompany: Company? = null

    fun setParentCompany(id: Int, name: String?, logoPath: String?) {
        val parent = Company()
        parent.id = id
        parent.name = name
        parent.logoPath = logoPath
        parentCompany = parent
    }
}
