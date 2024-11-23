package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.changeBrightness
import com.cornellappdev.resell.android.util.toSortedPair
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/**
 * Requires: grid is non-empty
 */
@Composable
private fun SelectableGrid(
    grid: List<BooleanArray>,
    updateGrid: ((List<BooleanArray>) -> List<BooleanArray>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFirstMove by remember { mutableStateOf(true) }
    var isRemoving by remember { mutableStateOf(false) }
    val (width, height) = grid.first().size to grid.size
    var selectionStart by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectionEnd by remember { mutableStateOf<Pair<Int, Int>?>(null) }


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

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(grid) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val position = event.calculateCentroid()
                    if (event.type == PointerEventType.Move) {
                        if (isFirstMove) {
                            val (row, col) = getGridCell(position, size.toSize(), width, height)
                            selectionStart = row to col
                            isRemoving = grid[row][col]
                            isFirstMove = false
                        }
                        selectionEnd = getGridCell(position, size.toSize(), width, height)
                    } else if (event.type == PointerEventType.Release) {
                        updateGrid {
                            it.changeBySelection(!isRemoving)
                        }
                        isRemoving = false
                        selectionStart = null
                        selectionEnd = null
                        isFirstMove = true
                    }

                    event.changes.forEach {
                        val offset = it.positionChange()
                        if (abs(offset.y) > 0f) {
                            it.consume()
                        }
                    }
                }
            }
        }) {
        val rectWidth = size.width / width
        val rectHeight = size.height / height

        /**
         * We don't want to subdivide the grid too much vertically or it will be overwhelming
         * for the user. So we only show half of the vertical grid cells, unless they are filled
         * in.
         */

        // Draw border
        drawBorder(grid, rectWidth, rectHeight)

        // Draw selected grid cells
        drawSelectedGridCells(grid, rectWidth, rectHeight)

        // Draw selection preview
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


@Composable
fun SelectableAvailabilityGrid(
    dates: List<LocalDate>,
    selectedAvailabilities: List<LocalDateTime>,
    setSelectedAvailabilities: (List<LocalDateTime>) -> Unit,
    modifier: Modifier = Modifier
) {
    var grid by remember {
        mutableStateOf(
            selectedAvailabilities.mapToGrid(dates)
        )
    }


    AvailabilityGridContainer(dates, modifier) {
        SelectableGrid(
            grid = grid,
            updateGrid = {
                val newGrid = it(grid)
                grid = newGrid
                setSelectedAvailabilities(newGrid.toAvailabilities(dates))
            },
        )
    }
}


@Preview
@Composable
private fun AvailabilityGrid_RUNME_Preview() = ResellPreview {
    var selectedAvailabilities by remember { mutableStateOf(emptyList<LocalDateTime>()) }
    Column {
        Text(
            "Selected availabilities: ${
                selectedAvailabilities
                    .joinToString(", ")
                    { it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
            }",
            style = Style.body1
        )
        SelectableAvailabilityGrid(
            testDates,
            selectedAvailabilities,
            setSelectedAvailabilities = {
                selectedAvailabilities = it
            },
            modifier = Modifier.weight(1F)
        )
    }
}
