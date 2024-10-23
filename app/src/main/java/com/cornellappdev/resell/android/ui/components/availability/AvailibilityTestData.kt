package com.cornellappdev.resell.android.ui.components.availability

import java.time.LocalDateTime

val dates = listOf(
    LocalDateTime.now().minusDays(1L),
    LocalDateTime.now(),
    LocalDateTime.now().plusDays(1L)
)

val availabilities = dates.flatMap { date ->
    buildList {
        val startDate = LocalDateTime.of(date.toLocalDate(), gridStartTime)
        repeat(GRID_HEIGHT - 1) { i ->
            if (Math.random() < 0.25) {
                add(startDate.plusMinutes(i * 30L))
            }
        }
    }
}