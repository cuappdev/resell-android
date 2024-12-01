package com.cornellappdev.resell.android.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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

/**
 * Returns the MONTH DAY, YEAR format.
 */
fun Timestamp.toDateString(): String {
    val date = this.toDate()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("GMT-5") // UTC-5
    return dateFormat.format(date)
}

fun LocalDateTime.convertToFirestoreTimestamp(): Timestamp {
    // Convert LocalDateTime to Instant using UTC-5 offset
    val instant = this.toInstant(ZoneOffset.ofHours(-5))

    // Create Firestore Timestamp from Instant
    return Timestamp(instant.epochSecond, instant.nano)
}
