package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.changeBrightness
import com.cornellappdev.resell.android.util.toSortedPair
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

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
        AvailabilityGrid(
            listOf(
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)
            ),
            setSelectedAvailabilities = {
                selectedAvailabilities = it
            },
            modifier = Modifier.weight(1F)
        )
    }
}

@Composable
fun AvailabilityGrid(
    dates: List<LocalDateTime>,
    setSelectedAvailabilities: (List<LocalDateTime>) -> Unit,
    modifier: Modifier = Modifier
) {
    val width = dates.size
    val height = 24
    val dayStart = LocalTime.of(9, 0)

    var grid by remember {
        mutableStateOf(
            buildList {
                repeat(height) {
                    add(BooleanArray(width))
                }
            }
        )
    }

    fun List<BooleanArray>.toAvailabilities(): List<LocalDateTime> =
        flatMapIndexed { row, cells ->
            cells.mapIndexed { col, filled ->
                if (!filled) {
                    null
                } else {
                    val day = dates[col]
                    day
                        .withHour(dayStart.hour)
                        .withMinute(dayStart.minute)
                        .withSecond(dayStart.second)
                        .withNano(dayStart.nano)
                        .plusMinutes(30L * row)
                }
            }.filterNotNull()
        }


    fun getTimeForRow(row: Int): LocalTime {
        return dayStart.plusHours(1L * row)
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1F)) {
            Spacer(modifier = Modifier.weight(1F))
            Row(
                modifier = Modifier
                    .weight(3F)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                dates.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            it.format(DateTimeFormatter.ofPattern("E")),
                            style = Style.title1
                        )
                        Text(
                            it.format(DateTimeFormatter.ofPattern("MMM d")),
                            style = Style.body1
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.weight(height.toFloat() / 2)) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                repeat(height / 2) { index ->
                    Text(
                        getTimeForRow(index).format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = Style.title2
                    )
                }
            }
            SelectableGrid(
                grid = grid,
                updateGrid = {
                    val newGrid = it(grid)
                    grid = newGrid
                    setSelectedAvailabilities(newGrid.toAvailabilities())
                },
                modifier = Modifier.weight(width.toFloat())
            )
        }
    }
}

/**
 * Requires: grid is non-empty
 */
@Composable
private fun SelectableGrid(
    grid: List<BooleanArray>,
    updateGrid: ((List<BooleanArray>) -> List<BooleanArray>) -> Unit,
    modifier: Modifier = Modifier,
    gridStroke: Color = Stroke,
    fillColor: Color = ResellPurple,
) {
    var isFirstMove by remember { mutableStateOf(true) }
    var isRemoving by remember { mutableStateOf(false) }
    val (width, height) = grid.first().size to grid.size
    var selectionStart by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectionEnd by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    fun getGridCell(offset: Offset, canvasSize: Size): Pair<Int, Int> {
        val gridCol = floor(offset.x / (canvasSize.width / width)).toInt().coerceIn(0, width - 1)
        val gridRow = floor(offset.y / (canvasSize.height / height)).toInt().coerceIn(0, height - 1)
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

    Canvas(modifier = modifier
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

        /**
         * We don't want to subdivide the grid too much vertically or it will be overwhelming
         * for the user. So we only show half of the vertical grid cells, unless they are filled
         * in.
         */

        // Draw border
        for (row in grid.indices.filter { it % 2 == 0 }) {
            for (col in grid[row].indices) {
                val position = Offset(rectWidth * col, rectHeight * row)

                drawRect(
                    size = Size(rectWidth, rectHeight * 2),
                    topLeft = position,
                    color = gridStroke,
                    style = Stroke(width = 4F)
                )
            }
        }

        // Draw selected grid cells
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col]) {
                    val position = Offset(rectWidth * col, rectHeight * row)

                    drawRect(
                        size = Size(rectWidth, rectHeight),
                        topLeft = position,
                        color = fillColor,
                        style = Fill
                    )
                }
            }
        }

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