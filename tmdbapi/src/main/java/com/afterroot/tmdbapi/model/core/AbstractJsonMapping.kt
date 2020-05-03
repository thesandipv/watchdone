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
        val sb = StringBuilder()
        sb.append("Unknown property: '").append(key)
        sb.append("' value: '").append(value).append("'")
        Log.d(TAG, "handleUnknown: $sb")
    }

    companion object {
        private const val TAG = "AbstractJsonMapping"
    }

    //abstract fun type() : Int
}