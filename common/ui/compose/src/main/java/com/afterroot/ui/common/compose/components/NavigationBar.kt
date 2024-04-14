package com.afterroot.ui.common.compose.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollAwareNavigationBar(
  modifier: Modifier = Modifier,
  containerColor: Color = NavigationBarDefaults.containerColor,
  contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
  tonalElevation: Dp = NavigationBarDefaults.Elevation,
  windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
  scrollBehavior: TopAppBarScrollBehavior?,
  content: @Composable RowScope.() -> Unit,
) {
  val navigationHeight = 80.dp
  val heightOffsetLimit = with(LocalDensity.current) { -navigationHeight.toPx() }
  SideEffect {
    if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
      scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
    }
  }

  val appBarDragModifier = if (scrollBehavior != null && !scrollBehavior.isPinned) {
    Modifier.draggable(
      orientation = Orientation.Vertical,
      state = rememberDraggableState { delta ->
        scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
      },
      onDragStopped = { velocity ->
        settleAppBar(
          scrollBehavior.state,
          velocity,
          scrollBehavior.flingAnimationSpec,
          scrollBehavior.snapAnimationSpec,
        )
      },
    )
  } else {
    Modifier
  }

  val offset = LocalDensity.current.run {
    (scrollBehavior?.state?.heightOffset ?: 0f).toDp()
  }

  NavigationBar(
    modifier = modifier
      .then(appBarDragModifier)
      .offset(y = -offset),
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    windowInsets = windowInsets,
    content = content,
  )
}

@ExperimentalMaterial3Api
@Composable
fun navigationBarEnterAlwaysScrollBehavior(
  state: TopAppBarState = rememberTopAppBarState(),
  canScroll: () -> Boolean = { true },
  snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
  flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): TopAppBarScrollBehavior =
  EnterAlwaysScrollBehavior(
    state = state,
    snapAnimationSpec = snapAnimationSpec,
    flingAnimationSpec = flingAnimationSpec,
    canScroll = canScroll,
  )

@OptIn(ExperimentalMaterial3Api::class)
private class EnterAlwaysScrollBehavior(
  override val state: TopAppBarState,
  override val snapAnimationSpec: AnimationSpec<Float>?,
  override val flingAnimationSpec: DecayAnimationSpec<Float>?,
  val canScroll: () -> Boolean = { true },
) : TopAppBarScrollBehavior {
  override val isPinned: Boolean = false
  override var nestedScrollConnection =
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!canScroll()) return Offset.Zero
        val prevHeightOffset = state.heightOffset
        state.heightOffset += available.y
        return Offset.Zero
                /*return if (prevHeightOffset != state.heightOffset) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }*/
      }

      override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
      ): Offset {
        if (!canScroll()) return Offset.Zero
        state.contentOffset += consumed.y
        if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
          if (consumed.y == 0f && available.y > 0f) {
            // Reset the total content offset to zero when scrolling all the way down.
            // This will eliminate some float precision inaccuracies.
            state.contentOffset = 0f
          }
        }
        state.heightOffset += consumed.y
        return Offset.Zero
      }

      override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val superConsumed = super.onPostFling(consumed, available)
        return superConsumed + settleAppBar(
          state,
          available.y,
          flingAnimationSpec,
          snapAnimationSpec,
        )
      }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun settleAppBar(
  state: TopAppBarState,
  velocity: Float,
  flingAnimationSpec: DecayAnimationSpec<Float>?,
  snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
  // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
  // and just return Zero Velocity.
  // Note that we don't check for 0f due to float precision with the collapsedFraction
  // calculation.
  if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
    return Velocity.Zero
  }
  var remainingVelocity = velocity
  // In case there is an initial velocity that was left after a previous user fling, animate to
  // continue the motion to expand or collapse the app bar.
  if (flingAnimationSpec != null && abs(velocity) > 1f) {
    var lastValue = 0f
    AnimationState(
      initialValue = 0f,
      initialVelocity = velocity,
    )
      .animateDecay(flingAnimationSpec) {
        val delta = value - lastValue
        val initialHeightOffset = state.heightOffset
        state.heightOffset = initialHeightOffset + delta
        val consumed = abs(initialHeightOffset - state.heightOffset)
        lastValue = value
        remainingVelocity = this.velocity
        // avoid rounding errors and stop if anything is unconsumed
        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
      }
  }
  // Snap if animation specs were provided.
  if (snapAnimationSpec != null) {
    if (state.heightOffset < 0 &&
      state.heightOffset > state.heightOffsetLimit
    ) {
      AnimationState(initialValue = state.heightOffset).animateTo(
        if (state.collapsedFraction < 0.5f) {
          0f
        } else {
          state.heightOffsetLimit
        },
        animationSpec = snapAnimationSpec,
      ) { state.heightOffset = value }
    }
  }

  return Velocity(0f, remainingVelocity)
}
