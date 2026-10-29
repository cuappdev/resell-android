package com.cornellappdev.resell.android.model.chats

import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.api.StartAndEnd
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ChatDocument(
    val id: String,
    val timestamp: Timestamp,
    /** Urls **/
    val images: List<String> = emptyList(),
    val text: String = "",
    val type: String,
    val senderId: String,
    val availabilities: List<StartAndEnd>?,
    val accepted: Boolean?,
    val cancellation: Boolean = false,
    val startDate: Timestamp?,
    val endDate: Timestamp?,
    val transactionId: String?
)

/**
 * The lifecycle of a meeting proposal.
 */
enum class MeetingState(val value: String) {
    PROPOSED("proposed"),
    CONFIRMED("confirmed"),
    DECLINED("declined"),
    CANCELED("canceled");

    companion object {
        fun fromWire(value: String?): MeetingState? = entries.firstOrNull { it.value == value }
    }
}

/**
 * An optional block included in the [ChatDocument] to represent a meeting proposal.
 */
data class MeetingInfo(
    val proposeTime: Timestamp,
    val state: MeetingState,
    var mostRecent: Boolean
) {
    val actionText
        get() = when (state) {
            MeetingState.PROPOSED -> "View Proposal"
            MeetingState.DECLINED -> "Send Another Proposal"
            MeetingState.CONFIRMED -> "View Details"
            MeetingState.CANCELED -> null
        }

    val icon
        get() = when (state) {
            MeetingState.DECLINED, MeetingState.CANCELED -> R.drawable.ic_slash
            MeetingState.PROPOSED, MeetingState.CONFIRMED -> R.drawable.ic_calendar
        }

    val endTime: Timestamp
        get() {
            val calendar = Calendar.getInstance()
            calendar.time = proposeTime.toDate()
            calendar.add(Calendar.MINUTE, 30)
            return Timestamp(calendar.time)
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

enum class TransactionState(val value: String) {
    COMPLETED("completed"),
    CANCELED("canceled");
}

data class TransactionInfo(
    val completeTime: Timestamp,
    val state: TransactionState,
    var mostRecent: Boolean
) {
    val actionText
        get() = when (state) {
            TransactionState.COMPLETED -> "Leave Review"
            else -> ""
        }
    val icon
        get() = when (state) {
            TransactionState.CANCELED -> R.drawable.ic_slash
            else -> R.drawable.ic_bag
        }
}

data class AvailabilityDocument(
    val availabilities: List<AvailabilityBlock>
)

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

