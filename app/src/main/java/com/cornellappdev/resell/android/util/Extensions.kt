package com.cornellappdev.resell.android.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

fun Pair<Int, Int>.toSortedPair() = if (first <= second) this else second to first

val LocalDateTime.day: Long get() = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), this)

val LocalDate.day: Long get() = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), this)

fun Timestamp.toIsoString(): String {
    // Convert Firebase Timestamp to Date
    val date = this.toDate()

    // Format Date to ISO 8601 string
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    isoFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure the output is in UTC

    return isoFormat.format(date)
}
