package com.cornellappdev.resell.android.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun Pair<Int, Int>.toSortedPair() = if (first <= second) this else second to first

val LocalDateTime.day: Long get() = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), this)