package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.changeBrightness
import com.cornellappdev.resell.android.util.toSortedPair
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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


@Composable
private fun ViewOnlyGrid(grid: List<BooleanArray>, onGridCellClick: (Pair<Int, Int>) -> Unit) {
    val (width, height) = grid.first().size to grid.size

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(grid) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()

                    if (event.type == PointerEventType.Press) {

                        val (row, col) = getGridCell(
                            event.changes.first().position,
                            size.toSize(),
                            width,
                            height
                        )
                        if (grid[row][col]) {
                            onGridCellClick(row to col)
                        }
                    }
                }
            }
        }) {
        val rectWidth = size.width / width
        val rectHeight = size.height / height

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
    }
}

@Composable
private fun RowScope.TimeColumn() {
    Column(
        modifier = Modifier.Companion
            .weight(1F)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(GRID_HEIGHT.toFloat() / 2F - 1F)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(GRID_HEIGHT / 2) { index ->
                Text(
                    getTimeForRow(index).format(DateTimeFormatter.ofPattern("hh:mm a")),
                    style = Style.title2
                )
            }
        }
        Spacer(
            modifier = Modifier
                .weight(.8F)
                .border(color = Color.Red, width = 1.dp)
        )
    }
}

@Composable
private fun RowScope.DateRow(dates: List<LocalDateTime>) {
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

@Composable
private fun AvailabilityGridContainer(
    dates: List<LocalDateTime>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1F)) {
            Spacer(modifier = Modifier.weight(1F))
            DateRow(dates)
        }
        Row(modifier = Modifier.weight(GRID_HEIGHT.toFloat() / 2)) {
            TimeColumn()

            Box(modifier = Modifier.weight(dates.size.toFloat())) {
                content()
            }
        }
    }
}

@Composable
fun ViewOnlyAvailabilityGrid(
    dates: List<LocalDateTime>,
    availabilities: List<LocalDateTime>,
    onSelectAvailability: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    AvailabilityGridContainer(dates, modifier) {
        ViewOnlyGrid(availabilities.mapToGrid(dates), onGridCellClick = { (row, col) ->
            onSelectAvailability(
                LocalDateTime.of(
                    dates[col].toLocalDate(),
                    gridStartTime.plusMinutes(30L * row)
                )
            )
        })
    }
}


@Composable
fun SelectableAvailabilityGrid(
    dates: List<LocalDateTime>,
    setSelectedAvailabilities: (List<LocalDateTime>) -> Unit,
    modifier: Modifier = Modifier
) {
    val width = dates.size

    var grid by remember {
        mutableStateOf(
            buildList {
                repeat(GRID_HEIGHT) {
                    add(BooleanArray(width))
                }
            }
        )
    }


    AvailabilityGridContainer(dates, modifier = modifier) {
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
            dates,
            setSelectedAvailabilities = {
                selectedAvailabilities = it
            },
            modifier = Modifier.weight(1F)
        )
    }
}

@Preview
@Composable
private fun ViewOnlyAvailabilityGrid_RUNME_Preview() {
    var selectedTime by remember { mutableStateOf(LocalDateTime.now()) }
    Column {
        Text("Selected time: $selectedTime")
        ViewOnlyAvailabilityGrid(
            dates, availabilities, onSelectAvailability = {
                selectedTime = it
            }
        )
    }
}