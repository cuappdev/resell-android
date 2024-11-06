package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDate
import java.time.LocalDateTime

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

        drawBorder(grid, rectWidth, rectHeight)

        drawSelectedGridCells(grid, rectWidth, rectHeight)
    }
}

@Composable
fun ViewOnlyAvailabilityGrid(
    dates: List<LocalDate>,
    availabilities: List<LocalDateTime>,
    onSelectAvailability: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    AvailabilityGridContainer(dates, modifier) {
        ViewOnlyGrid(availabilities.mapToGrid(dates), onGridCellClick = { (row, col) ->
            onSelectAvailability(
                LocalDateTime.of(
                    dates[col],
                    gridStartTime.plusMinutes(30L * row)
                )
            )
        })
    }
}

@Preview
@Composable
private fun ViewOnlyAvailabilityGrid_RUNME_Preview() = ResellPreview {
    var selectedTime by remember { mutableStateOf(LocalDateTime.now()) }
    Column {
        Text("Selected time: $selectedTime")
        ViewOnlyAvailabilityGrid(
            testDates, availabilities, onSelectAvailability = {
                selectedTime = it
            }
        )
    }
}