package com.cornellappdev.resell.android.model.profile

import com.cornellappdev.resell.android.model.api.AvailabilitySlot
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.UpdateAvailabilityRequest
import com.cornellappdev.resell.android.model.api.UserAvailability
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvailabilityRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getMyAvailability(): UserAvailability {
        return retrofitInstance.availabilityApi.getMyAvailability().availability
    }

    suspend fun updateAvailability(slots: List<LocalDateTime>): UserAvailability {
        // Convert List<LocalDateTime> to Map<"yyyy-MM-dd", List<AvailabilitySlot>>
        val schedule = slots
            .groupBy { it.toLocalDate().toString() }
            .mapValues { (_, daySlots) ->
                daySlots.sorted().map { start ->
                    AvailabilitySlot(
                        startDate = start.toUtcInstantString(),
                        endDate = start.plusMinutes(30L).toUtcInstantString()
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