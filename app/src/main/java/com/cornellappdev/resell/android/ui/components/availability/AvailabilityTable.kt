package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import kotlin.math.floor


@Composable
internal fun SelectableGrid(
    width: Int,
    height: Int,
    selectionStart: Pair<Int, Int>?,
    selectionEnd: Pair<Int, Int>?,
    setSelectionStart: (Pair<Int, Int>?) -> Unit,
    setSelectionEnd: (Pair<Int, Int>?) -> Unit,
) {
    var isFirstMove by remember { mutableStateOf(false) }

    fun getGridCell(offset: Offset, size: Size): Pair<Int, Int> {
        val gridCol = floor(offset.x / (size.width / width)).toInt()
        val gridRow = floor(offset.y / (size.height / width)).toInt()
        return gridCol to gridRow
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(null) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val position = event.calculateCentroid()
                    if (event.type == PointerEventType.Press) {
                        isFirstMove = true
                        setSelectionStart(null)
                        setSelectionEnd(null)
                    }
                    if (event.type == PointerEventType.Move) {
                        if (isFirstMove) {
                            setSelectionStart(getGridCell(position, size.toSize()))
                            isFirstMove = false
                        }
                        setSelectionEnd(getGridCell(position, size.toSize()))
                    }
                }
            }
        }) {
        val rectWidth = size.width / width
        val rectHeight = size.height / height
        var position = Offset(0F, 0F)

        while (position.y < size.height) {
            while (position.x < size.width) {
                val gridCell = getGridCell(Offset(position.x, position.y), size)
                val style = selectionStart?.let { start ->
                    selectionEnd?.let { end ->
                        val leftCol = minOf(start.first, end.first)
                        val rightCol = maxOf(start.first, end.first)

                        val topRow = minOf(start.second, end.second)
                        val bottomRow = maxOf(start.second, end.second)

                        if (gridCell.first in leftCol..rightCol && gridCell.second in topRow..bottomRow) {
                            Fill
                        } else {
                            Stroke(width = 4F)
                        }
                    }
                } ?: Stroke(width = 4F)

                drawRect(
                    size = Size(rectWidth, rectHeight),
                    topLeft = position,
                    color = Color.hsv(
                        126F,
                        200F / 255F,
                        100F / 255F,
                        position.x / size.width + position.y / size.height
                    ),
                    style = style
                )
                position = position.copy(x = position.x + rectWidth)
            }
            position = position.copy(x = 0F, y = position.y + rectHeight)
        }
    }
}

@Preview
@Composable
private fun AvailabilityTablePreview() = ResellPreview {
    var startGridCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var endGridCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    SelectableGrid(5, 20, startGridCell, endGridCell, setSelectionStart = {
        startGridCell = it
    }, setSelectionEnd = { endGridCell = it })
}