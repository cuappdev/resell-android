package com.cornellappdev.resell.android.ui.components.availability.helper

import java.time.LocalDate
import java.time.LocalDateTime

val testDates = listOf(
    LocalDate.now().minusDays(1L),
    LocalDate.now(),
    LocalDate.now().plusDays(1L)
)

val availabilities = testAvailabilities(testDates)

fun testAvailabilities(dates: List<LocalDate>): List<LocalDateTime> = dates.flatMap { date ->
    buildList {
        val startDate = LocalDateTime.of(date, gridStartTime)
        repeat(GRID_HEIGHT - 1) { i ->
            if (Math.random() < 0.25) {
                add(startDate.plusMinutes(i * 30L))
            }
        }
    }
}