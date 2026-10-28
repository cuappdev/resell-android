package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.classes.Listing
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

fun Pair<Int, Int>.toSortedPair() = if (first <= second) this else second to first

fun List<Listing>.excludingIds(seen: List<Listing>): List<Listing> {
    val seenIds = seen.map { it.id }.toSet()
    return filterNot { it.id in seenIds }
}

val LocalDateTime.day: Long get() = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), this)

val LocalDate.day: Long get() = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), this)
fun Timestamp.toIsoString(): String = toDate().toInstant().toString()

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
    //  For some reason this became -4 after backend refactor? Should be -5
    val instant = this.toInstant(ZoneOffset.ofHours(-4))

    // Create Firestore Timestamp from Instant
    return Timestamp(instant.epochSecond, instant.nano)
}
