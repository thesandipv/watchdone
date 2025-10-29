/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.base.compose

import app.tivi.api.UiMessage

abstract class ViewState {
  abstract val message: UiMessage?
}
