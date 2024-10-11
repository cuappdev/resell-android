package com.cornellappdev.resell.android.ui.components.availability

import android.util.Log
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
import com.cornellappdev.resell.android.util.toSortedPair
import kotlin.math.floor

// TODO convert this to grid so I can make multiple selections and erase portions of selections
/**
 * Requires: grid is non-empty
 */
@Composable
fun SelectableGrid(
    grid: List<BooleanArray>,
    updateGrid: ((List<BooleanArray>) -> List<BooleanArray>) -> Unit,
) {
    var isFirstMove by remember { mutableStateOf(true) }
    var isRemoving by remember { mutableStateOf(false) }
    val (width, height) = grid.first().size to grid.size
    var selectionStart by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectionEnd by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    fun getGridCell(offset: Offset, canvasSize: Size): Pair<Int, Int> {
        val gridCol = floor(offset.x / (canvasSize.width / width)).toInt()
        val gridRow = floor(offset.y / (canvasSize.height / height)).toInt()
        return gridRow to gridCol
    }

    fun inSelection(row: Int, col: Int): Boolean = selectionStart?.let { selectionStart ->
        selectionEnd?.let { selectionEnd ->
            val (minRow, maxRow) = (selectionStart.first to selectionEnd.first).toSortedPair()
            val (minCol, maxCol) = (selectionStart.second to selectionEnd.second).toSortedPair()
            row in minRow..maxRow && col in minCol..maxCol
        } ?: false
    } ?: false

    fun List<BooleanArray>.changeBySelection(selection: Boolean): List<BooleanArray> {
        return mapIndexed { row, boolRow ->
            boolRow
                .mapIndexed { col, value ->
                    if (inSelection(row, col)) {
                        selection
                    } else {
                        value
                    }
                }
                .toBooleanArray()
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(null) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val position = event.calculateCentroid()
                    if (event.type == PointerEventType.Move) {
                        if (isFirstMove) {
                            val (row, col) = getGridCell(position, size.toSize())
                            selectionStart = row to col
                            isRemoving = grid[row][col]
                            isFirstMove = false
                        }
                        selectionEnd = getGridCell(position, size.toSize())
                    } else if (event.type == PointerEventType.Release) {
                        if (isRemoving) {
                            updateGrid { it.changeBySelection(false) }
                        } else {
                            Log.d("TAG", "SelectableGrid: selection start = $selectionStart")
                            updateGrid { it.changeBySelection(true) }
                        }
                        selectionStart = null
                        selectionEnd = null
                        isFirstMove = true
                    }
                }
            }
        }) {
        val rectWidth = size.width / width
        val rectHeight = size.height / height

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val style = if (grid[row][col]) {
                    Fill
                } else {
                    Stroke(width = 4f)
                }
                val position = Offset(rectWidth * col, rectHeight * row)
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
            }
        }
    }
}

@Preview
@Composable
private fun AvailabilityTablePreview() = ResellPreview {
    var grid by remember {
        mutableStateOf(
            listOf(
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
                BooleanArray(5),
            )
        )
    }
    SelectableGrid(grid, updateGrid = {
        grid = it(grid)
    })
}