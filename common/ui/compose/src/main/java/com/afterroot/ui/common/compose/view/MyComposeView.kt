/*
 * Copyright (C) 2020-2020 Sandip Vaghela
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
package com.afterroot.ui.common.compose.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

/**
 * Modified Version of [androidx.compose.ui.platform.ComposeView]
 *
 * This ViewGroup initializes as transition group.
 *
 * A [android.view.View] that can host Jetpack Compose UI content.
 * Use [setContent] to supply the content composable function for the view.
 *
 * By default, the composition is disposed according to [ViewCompositionStrategy.Default].
 * Call [disposeComposition] to dispose of the underlying composition earlier, or if the view is
 * never initially attached to a window. (The requirement to dispose of the composition explicitly
 * in the event that the view is never (re)attached is temporary.)
 */
class MyComposeView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

  init {
    isTransitionGroup = true
  }

  private val content = mutableStateOf<(@Composable () -> Unit)?>(null)

  @Suppress("RedundantVisibilityModifier")
  protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
    private set

  @Composable
  override fun Content() {
    content.value?.invoke()
  }

  override fun getAccessibilityClassName(): CharSequence {
    return javaClass.name
  }

  // Override addView until fixed https://issuetracker.google.com/issues/236561967
  override fun addView(child: View?) {
    // super.addView(child)
    isTransitionGroup = true
  }

  override fun addView(child: View?, index: Int) {
    // super.addView(child, index)
    isTransitionGroup = true
  }

  /**
   * Set the Jetpack Compose UI content for this view.
   * Initial composition will occur when the view becomes attached to a window or when
   * [createComposition] is called, whichever comes first.
   */
  fun setContent(content: @Composable () -> Unit) {
    shouldCreateCompositionOnAttachedToWindow = true
    this.content.value = content
    if (isAttachedToWindow) {
      createComposition()
    }
  }
}
