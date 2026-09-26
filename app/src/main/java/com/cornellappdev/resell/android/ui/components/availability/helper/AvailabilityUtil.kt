package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.day
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.floor

const val GRID_HEIGHT = 24
const val SLOT_DURATION_MINUTES = 30
val gridStartTime: LocalTime = LocalTime.of(9, 0)
val gridStroke = Stroke
val fillColor = ResellPurple

/** Minimum horizontal drag distance before a swipe on [MonthCalendar] changes the month. */
val MonthSwipeThreshold: Dp = 56.dp

/**
 * Caps [MonthCalendar]'s day-grid at the height of 5 rows (most common case).
 * A 6-row month only occurs when the 1st falls on a Fri/Sat in a 30/31-day month,
 * so in this case it scrolls internally instead of growing past this, so the
 * header stays pinned and the surrounding panel never has to resize.
 */
val MonthCalendarGridMaxHeight: Dp = 248.dp

/** Number of day columns an availability grid shows at once. */
const val DAY_WINDOW_SIZE = 3

/**
 * Returns the [DAY_WINDOW_SIZE]-day window whose first column is [startDate], rolling into the
 * next month as needed. Deliberately does not snap to calendar buckets — the caller's date is
 * always the leftmost column, so a window anchored at today never shows a past day.
 */
fun dayWindowStartingAt(startDate: LocalDate): List<LocalDate> =
    (0 until DAY_WINDOW_SIZE).map { startDate.plusDays(it.toLong()) }


fun getGridCell(offset: Offset, canvasSize: Size, width: Int, height: Int): Pair<Int, Int> {
    val gridCol = floor(offset.x / (canvasSize.width / width)).toInt().coerceIn(0, width - 1)
    val gridRow = floor(offset.y / (canvasSize.height / height)).toInt().coerceIn(0, height - 1)
    return gridRow to gridCol
}

fun List<BooleanArray>.toAvailabilities(dates: List<LocalDate>): List<LocalDateTime> =
    flatMapIndexed { row, cells ->
        cells.mapIndexed { col, filled ->
            if (!filled) {
                null
            } else {
                rowColToLocalDateTime(row, col, dates)
            }
        }.filterNotNull()
    }

fun rowColToLocalDateTime(row: Int, col: Int, dates: List<LocalDate>): LocalDateTime {
    val day = LocalDateTime.of(dates.getOrNull(col), LocalTime.now())
    return day
        .withHour(gridStartTime.hour)
        .withMinute(gridStartTime.minute)
        .withSecond(gridStartTime.second)
        .withNano(gridStartTime.nano)
        .plusMinutes(SLOT_DURATION_MINUTES.toLong() * row)
}

fun getTimeForRow(row: Int): LocalTime {
    return gridStartTime.plusHours(1L * row)
}

fun List<LocalDateTime>.mapToGrid(dates: List<LocalDate>): List<BooleanArray> {
    val grid = buildList {
        repeat(GRID_HEIGHT) {
            add(BooleanArray(dates.size))
        }
    }.toMutableList()
    forEach { date ->
        val column = dates.indexOfFirst { it.day == date.day }
        if (column == -1) return@forEach
        val row = (date.hour * 60 + date.minute - gridStartTime.hour * 60) / SLOT_DURATION_MINUTES
        if (row !in 0 until GRID_HEIGHT) return@forEach
        grid[row][column] = true
    }
    return grid
}

/**
 * Greys out every cell where [unavailableGrid] is true, so only cells left white/normal
 * represent times available to both parties.
 */
fun DrawScope.drawUnavailableCells(unavailableGrid: List<BooleanArray>, rectWidth: Float, rectHeight: Float) {
    for (row in unavailableGrid.indices) {
        for (col in unavailableGrid[row].indices) {
            if (unavailableGrid[row][col]) {
                val position = Offset(rectWidth * col, rectHeight * row)

                drawRect(
                    size = Size(rectWidth, rectHeight),
                    topLeft = position,
                    color = Wash,
                    style = Fill
                )
            }
        }
    }
}

fun DrawScope.drawBorder(grid: List<BooleanArray>, rectWidth: Float, rectHeight: Float) {
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
}

fun DrawScope.drawSelectedGridCells(grid: List<BooleanArray>, rectWidth: Float, rectHeight: Float) {
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
