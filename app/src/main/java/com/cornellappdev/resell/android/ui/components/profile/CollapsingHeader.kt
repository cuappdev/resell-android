package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

@Stable
class CollapsingHeaderState(initialOffsetPx: Float = 0f) {
    var heightPx: Float = 0f
        internal set

    var offsetPx by mutableFloatStateOf(initialOffsetPx)
        private set

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

    companion object {
        val Saver = Saver<CollapsingHeaderState, Float>(
            save = { it.offsetPx },
            restore = { CollapsingHeaderState(it) }
        )
    }
}

@Composable
fun rememberCollapsingHeaderState(): CollapsingHeaderState =
    rememberSaveable(saver = CollapsingHeaderState.Saver) { CollapsingHeaderState() }

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
