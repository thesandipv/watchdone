/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.common

import android.view.View

interface ItemSelectedCallback<T> {
  fun onClick(position: Int, view: View? = null) {}
  fun onClick(position: Int, view: View? = null, item: T) {}
  fun onLongClick(position: Int, item: T) {}
}
