package com.afterroot.tmdbapi.model.core

import android.util.Log
import com.fasterxml.jackson.annotation.JsonAnySetter
import java.io.Serializable

/**
 * @author Sandip Vaghela
 */
abstract class AbstractJsonMapping : Serializable {
    @JsonAnySetter
    open fun handleUnknown(key: String?, value: Any?) {
        val unknown = "Unknown property: '$key' value: '$value'"
        if (System.getenv("Test").isNullOrBlank()) { //Not in test environment
            Log.d(TAG, "handleUnknown: $unknown")
        } else { //In test environment
            println("handleUnknown: $unknown")
        }
    }

    companion object {
        private const val TAG = "AbstractJsonMapping"
    }

    //abstract fun type() : Int
}