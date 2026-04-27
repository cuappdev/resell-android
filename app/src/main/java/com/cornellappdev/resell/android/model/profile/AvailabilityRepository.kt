package com.cornellappdev.resell.android.model.profile

import com.cornellappdev.resell.android.model.api.AvailabilitySlot
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.UpdateAvailabilityRequest
import com.cornellappdev.resell.android.model.api.UserAvailability
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
                daySlots.sortedBy { it }.map { start ->
                    AvailabilitySlot(
                        startDate = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        endDate = start.plusMinutes(30L)
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
            }

        return retrofitInstance.availabilityApi.updateAvailability(
            UpdateAvailabilityRequest(schedule = schedule)
        ).availability
    }
}