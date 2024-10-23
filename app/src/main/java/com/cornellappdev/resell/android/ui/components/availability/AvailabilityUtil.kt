package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.util.day
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.floor

const val GRID_HEIGHT = 24
val gridStartTime: LocalTime = LocalTime.of(9, 0)
val gridStroke = Stroke
val fillColor = ResellPurple


fun getGridCell(offset: Offset, canvasSize: Size, width: Int, height: Int): Pair<Int, Int> {
    val gridCol = floor(offset.x / (canvasSize.width / width)).toInt().coerceIn(0, width - 1)
    val gridRow = floor(offset.y / (canvasSize.height / height)).toInt().coerceIn(0, height - 1)
    return gridRow to gridCol
}

fun List<BooleanArray>.toAvailabilities(dates: List<LocalDateTime>): List<LocalDateTime> =
    flatMapIndexed { row, cells ->
        cells.mapIndexed { col, filled ->
            if (!filled) {
                null
            } else {
                val day = dates.getOrNull(col) ?: return@mapIndexed null
                day
                    .withHour(gridStartTime.hour)
                    .withMinute(gridStartTime.minute)
                    .withSecond(gridStartTime.second)
                    .withNano(gridStartTime.nano)
                    .plusMinutes(30L * row)
            }
        }.filterNotNull()
    }

fun getTimeForRow(row: Int): LocalTime {
    return gridStartTime.plusHours(1L * row)
}

fun List<LocalDateTime>.mapToGrid(dates: List<LocalDateTime>): List<BooleanArray> {
    val grid = buildList {
        repeat(GRID_HEIGHT) {
            add(BooleanArray(dates.size))
        }
    }.toMutableList()
    forEach { date ->
        val column = dates.indexOfFirst { it.day == date.day }
        val row = (date.hour * 60 + date.minute - 9 * 60) / 30
        grid[row][column] = true
    }
    return grid
}