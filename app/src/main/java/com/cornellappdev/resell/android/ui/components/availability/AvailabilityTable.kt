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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.changeBrightness
import com.cornellappdev.resell.android.util.toSortedPair
import kotlin.math.floor

/**
 * Requires: grid is non-empty
 */
@Composable
fun SelectableGrid(
    grid: List<BooleanArray>,
    updateGrid: ((List<BooleanArray>) -> List<BooleanArray>) -> Unit,
    gridStroke: Color = Stroke,
    fillColor: Color = ResellPurple,
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
        return this.mapIndexed { row, boolRow ->
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
        .pointerInput(grid) {
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
                        updateGrid {
                            it.changeBySelection(!isRemoving)
                        }
                        isRemoving = false
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
                    color = if (grid[row][col]) fillColor else gridStroke,
                    style = style
                )
            }
        }
        selectionStart?.let { selectionStart ->
            selectionEnd?.let { selectionEnd ->
                val (minRow, maxRow) = (selectionStart.first to selectionEnd.first).toSortedPair()
                val (minCol, maxCol) = (selectionStart.second to selectionEnd.second).toSortedPair()
                val position = Offset(rectWidth * minCol, rectHeight * minRow)
                drawRect(
                    size = Size(
                        (maxCol - minCol + 1) * rectWidth,
                        (maxRow - minRow + 1) * rectHeight,
                    ), topLeft = position,
                    color = gridStroke.changeBrightness(0.8F),
                    style = Stroke(
                        width = 8F, pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(25F, 25F)
                        )
                    )
                )
                drawRect(
                    size = Size(
                        (maxCol - minCol + 1) * rectWidth,
                        (maxRow - minRow + 1) * rectHeight,
                    ), topLeft = position,
                    color = fillColor.copy(alpha = .5F).changeBrightness(1.3F),
                    style = Fill
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