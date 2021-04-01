package com.afterroot.tmdbapi.model.people

import com.afterroot.tmdbapi.model.Multi
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.apache.commons.lang3.StringUtils
import java.util.ArrayList

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
class PersonPeople : Person(), Multi {
    var personType = PersonType.PERSON
    var department: String? = null // Crew
    var job: String? = null        // Crew
        set(job) {
            field = StringUtils.trimToEmpty(job)
        }
    var character: String? = null  // Cast
    var order = -1                 // Cast

    @JsonProperty("also_known_as")
    var aka: List<String> = ArrayList()

    @JsonProperty("biography")
    var biography: String? = null
        set(biography) {
            field = StringUtils.trimToEmpty(biography)
        }

    @JsonProperty("birthday")
    var birthday: String? = null
        set(birthday) {
            field = StringUtils.trimToEmpty(birthday)
        }

    @JsonProperty("deathday")
    var deathday: String? = null
        set(deathday) {
            field = StringUtils.trimToEmpty(deathday)
        }

    @JsonProperty("homepage")
    var homepage: String? = null
        set(homepage) {
            field = StringUtils.trimToEmpty(homepage)
        }

    @JsonProperty("place_of_birth")
    var birthplace: String? = null
        set(birthplace) {
            field = StringUtils.trimToEmpty(birthplace)
        }

    @JsonProperty("imdb_id")
    var imdbId: String? = null
        set(imdbId) {
            field = StringUtils.trimToEmpty(imdbId)
        }

    /**
     * Add a crew member
     *
     * @param id
     * @param name
     * @param profilePath
     * @param department
     * @param job
     */
    fun addCrew(id: Int, name: String?, profilePath: String?, department: String?, job: String?) {
        personType = PersonType.CREW
        character = ""
        order = -1
        this.department = department
        this.id = id
        this.job = job
        this.name = name
        this.profilePath = profilePath
    }

    /**
     * Add a cast member
     *
     * @param id
     * @param name
     * @param profilePath
     * @param character
     * @param order
     */
    fun addCast(id: Int, name: String?, profilePath: String?, character: String?, order: Int) {
        department = CAST_DEPARTMENT
        job = CAST_JOB
        personType = PersonType.CAST
        this.character = character
        this.id = id
        this.name = name
        this.order = order
        this.profilePath = profilePath
    }

    override val mediaType: Multi.MediaType
        get() = Multi.MediaType.PERSON

    companion object {
        // todo initializers should all go away
        /*
     * Static fields for default cast information
     */
        private const val CAST_DEPARTMENT = "acting"
        private const val CAST_JOB = "actor"
        private const val DEFAULT_STRING = ""
    }
}