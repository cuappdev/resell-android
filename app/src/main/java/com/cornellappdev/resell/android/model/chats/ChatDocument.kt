package com.cornellappdev.resell.android.model.chats

import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ChatDocument(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: AvailabilityDocument?,
    val product: Post?,
    val meetingInfo: MeetingInfo?
)

/**
 * A copy of [ChatDocument] that can be used to push any datatype for availability and product.
 *
 * WARNING: Only use for directly pushing to firebase. Otherwise, use [ChatDocument].
 */
data class ChatDocumentAny(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: Any,
    val product: Any
)

data class UserDocument(
    val _id: String,
    val avatar: String,
    val name: String
)

/**
 * An optional block included in the [ChatDocument] to represent a meeting proposal.
 *
 * @property state Either "confirmed" or "declined" or "proposed" or "canceled".
 * @property proposeTime of the form "MMMM dd yyyy, h:mm a"
 * @property proposer Email of the person who proposed the meeting.
 * @property canceler Email of the person who canceled the meeting.
 */
data class MeetingInfo(
    val proposer: String?,
    val proposeTime: String,
    val state: String,
    val canceler: String?,
) {
    fun convertToUtcMinusFiveDate(): Date {
        val inputFormat = SimpleDateFormat("MMMM dd yyyy, h:mm a", Locale.ENGLISH)

        // Input format UTC-5
        inputFormat.timeZone = TimeZone.getTimeZone("GMT-5")

        // Parse the date string
        val parsedDate = inputFormat.parse(proposeTime)

        // Convert the parsed date to UTC-5
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"))
        if (parsedDate != null) {
            calendar.time = parsedDate
        }

        return calendar.time
    }

    fun convertToMeetingString(): String {
        val date = convertToUtcMinusFiveDate()
        val thirtyMinutesLater = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"))
        thirtyMinutesLater.time = date
        thirtyMinutesLater.add(Calendar.MINUTE, 30)
        val first = SimpleDateFormat("EEEE, MMMM dd · h:mm", Locale.ENGLISH).format(date)
        val second = SimpleDateFormat("h:mm a", Locale.ENGLISH).format(thirtyMinutesLater.time)
        return "Meeting at $first - $second"
    }
}

data class AvailabilityDocument(
    val availabilities: List<AvailabilityBlock>
) {
    fun toFirebaseArray(): List<Any> {
        // Sort each availability, sorted by start date:
        val sortedAvailabilities = availabilities.sortedBy { it.startDate }
        return sortedAvailabilities
    }
}

data class AvailabilityBlock(
    val startDate: Timestamp,
    val color: String = ResellPurple.let {
        "#${Integer.toHexString(it.hashCode()).substring(2)}"
    },
    val id: Int,
) {
    val endDate: Timestamp
        get() {
            val instant = startDate.toDate().toInstant()
                .plusSeconds(60 * 30L)
            return Timestamp(instant.epochSecond, instant.nano)
        }
}

data class ProductDocument(
    @SerializedName("_id") val id: String,
)
