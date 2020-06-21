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