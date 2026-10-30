package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * The header collapses as soon as the content scrolls up, but only comes back once the content is
 * back at its top, like a header that is the first item of a scroll view.
 */
@Stable
class CollapsingHeaderState {
    var heightPx: Float = 0f
        internal set

    var offsetPx by mutableFloatStateOf(0f)
        private set

    val collapsedFraction: Float
        get() = if (heightPx <= 0f) 0f else (-offsetPx / heightPx).coerceIn(0f, 1f)

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            return Offset(0f, shift(available.y))
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (available.y <= 0f) return Offset.Zero
            return Offset(0f, shift(available.y))
        }
    }

    private fun shift(delta: Float): Float {
        val newOffset = (offsetPx + delta).coerceIn(-heightPx, 0f)
        val used = newOffset - offsetPx
        offsetPx = newOffset
        return used
    }
}

fun Modifier.collapsingHeader(state: CollapsingHeaderState): Modifier = this
    .clipToBounds()
    .layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
        )
        state.heightPx = placeable.height.toFloat()
        val offset = state.offsetPx.roundToInt()
        val visibleHeight = (placeable.height + offset).coerceAtLeast(0)
        layout(placeable.width, visibleHeight) {
            placeable.placeRelative(0, offset)
        }
    }
