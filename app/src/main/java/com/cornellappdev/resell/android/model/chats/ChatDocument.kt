package com.cornellappdev.resell.android.model.chats

import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.api.StartAndEnd
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ChatDocument(
    val id: String,
    val timestamp: Timestamp,
    /** Urls **/
    val images: List<String>?,
    val text: String?,
    val type: String,
    val senderId: String,
    val availabilities: List<StartAndEnd>?,
    val accepted: Boolean?,
    val cancellation: Boolean?,
    val startDate: Timestamp?,
    val endDate: Timestamp?,
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
    val product: Any,
)

/**
 * A copy of [ChatDocument] that can be used to push any datatype for meeting info.
 *
 * WARNING: Only use for directly pushing to firebase. Otherwise, use [ChatDocument].
 */
data class ChatDocumentAnyMeetingInfo(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val availability: Any = mapOf<String, Any>(),
    val product: Any = mapOf<String, Any>(),
    val user: UserDocument,
    val meetingInfo: Any
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
 */
data class MeetingInfo(
    val proposeTime: Timestamp,
    val state: String,
    var mostRecent: Boolean
) {
    val actionText
        get() = when (state) {
            "proposed" -> "View Proposal"
            "declined" -> "Send Another Proposal"
            "confirmed" -> "View Details"
            "canceled" -> null
            else -> ""
        }

    val icon
        get() = when (state) {
            "declined", "canceled" -> R.drawable.ic_slash
            else -> R.drawable.ic_calendar
        }

    val endTime: Timestamp
        get() {
            val calendar = Calendar.getInstance()
            calendar.time = proposeTime.toDate()
            calendar.add(Calendar.MINUTE, 30)
            return Timestamp(calendar.time)
        }

    fun toFirebaseMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["proposeTime"] = proposeTime
        map["state"] = state
        return map
    }

    fun convertToUtcMinusFiveDate(): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"))
        calendar.time = proposeTime.toDate()

        return calendar.time
    }

    /**
     * Form the meeting time string in the form "EEEE, MMMM dd · h:mm - h:mm a"
     */
    fun convertToMeetingString(): String {
        val date = convertToUtcMinusFiveDate()
        val thirtyMinutesLater = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"))
        thirtyMinutesLater.time = date
        thirtyMinutesLater.add(Calendar.MINUTE, 30)
        val first = SimpleDateFormat("EEEE, MMMM dd · h:mm", Locale.ENGLISH).format(date)
        val second = SimpleDateFormat("h:mm a", Locale.ENGLISH).format(thirtyMinutesLater.time)
        return "$first - $second"
    }
}

data class TransactionInfo(
    val completeTime: Timestamp,
    val state: String,
    var mostRecent: Boolean
) {
    val actionText
        get() = when (state) {
            "initiated" -> "View Details"
            "completed" -> "Leave Review"
            "canceled" -> null
            else -> ""
        }

    val icon
        get() = when (state) {
            "canceled" -> R.drawable.ic_slash
            else -> R.drawable.ic_bag
        }

    fun convertToUtcMinusFiveDate(): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"))
        calendar.time = completeTime.toDate()
        return calendar.time
    }

    fun convertToTransactionString(): String {
        val date = convertToUtcMinusFiveDate()
        val formatter = SimpleDateFormat("EEEE, MMMM dd · h:mm a", Locale.ENGLISH)
        return formatter.format(date)
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
