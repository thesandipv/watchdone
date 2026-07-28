/*
 * Copyright (C) 2021-2025 AfterROOT
 */

package com.afterroot.watchdone.data.model

import kotlin.collections.firstOrNull
import kotlinx.serialization.Serializable

@Serializable
data class OssLibrary(
  val groupId: String,
  val artifactId: String,
  val name: String = artifactId,
  val spdxLicenses: List<License>? = null,
  val unknownLicenses: List<License>? = null,
) {
  val license: License? = spdxLicenses?.firstOrNull() ?: unknownLicenses?.firstOrNull()

  @Serializable
  data class License(val name: String, val url: String)
}
