/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.providers

import androidx.core.content.FileProvider
import com.afterroot.watchdone.R

class MyFileProvider : FileProvider(R.xml.file_provider_paths)
