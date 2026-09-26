package com.cornellappdev.resell.android.model.profile

import com.cornellappdev.resell.android.model.api.AvailabilitySlot
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.UpdateAvailabilityRequest
import com.cornellappdev.resell.android.model.api.UserAvailability
import com.cornellappdev.resell.android.ui.components.availability.helper.SLOT_DURATION_MINUTES
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvailabilityRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getMyAvailability(): Set<LocalDateTime> {
        return retrofitInstance.availabilityApi.getMyAvailability().availability.toLocalDateTimes()
    }

    suspend fun getUserAvailability(userId: String): Set<LocalDateTime> {
        return retrofitInstance.availabilityApi.getUserAvailability(userId).availability.toLocalDateTimes()
    }

    suspend fun updateAvailability(slots: List<LocalDateTime>): UserAvailability {
        // Convert List<LocalDateTime> to Map<"yyyy-MM-dd", List<AvailabilitySlot>>
        val schedule = slots
            .groupBy { it.toLocalDate().toString() }
            .mapValues { (_, daySlots) ->
                daySlots.sorted().map { start ->
                    AvailabilitySlot(
                        startDate = start.toUtcInstantString(),
                        endDate = start.plusMinutes(SLOT_DURATION_MINUTES.toLong()).toUtcInstantString()
                    )
                }
            }

        return retrofitInstance.availabilityApi.updateAvailability(
            UpdateAvailabilityRequest(schedule = schedule)
        ).availability
    }
}

// The backend stores/returns dates as UTC instants (e.g. "2026-01-23T16:00:00.000Z"), so
// device-local wall-clock times must be converted to an instant before sending.
private fun LocalDateTime.toUtcInstantString(): String =
    atZone(ZoneId.systemDefault()).toInstant().toString()

// The backend sends startDate as a UTC instant (e.g. "2026-01-23T16:00:00.000Z"), so it's
// parsed as an Instant and converted to the device's local wall-clock time.
private fun UserAvailability.toLocalDateTimes(): Set<LocalDateTime> {
    return schedule.values.flatten().map { slot ->
        Instant.parse(slot.startDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }.toSet()
}