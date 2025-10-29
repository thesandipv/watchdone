/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.base

abstract class WatchdoneException : Exception()

class EmptyQuerySnapshotException(override val message: String? = null) : WatchdoneException()

class InvalidResultException(override val message: String? = null) : WatchdoneException()
