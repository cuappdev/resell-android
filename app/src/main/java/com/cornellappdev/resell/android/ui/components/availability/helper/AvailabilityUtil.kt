package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.util.day
import java.time.LocalDate
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
        .plusMinutes(30L * row)
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
        val row = (date.hour * 60 + date.minute - gridStartTime.hour * 60) / 30
        if (row !in 0 until GRID_HEIGHT) return@forEach
        grid[row][column] = true
    }
    return grid
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
