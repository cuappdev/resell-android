package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AvailabilityApiService {

    @GET("availability/")
    suspend fun getMyAvailability(): AvailabilityResponse

    @POST("availability/update/")
    suspend fun updateAvailability(
        @Body request: UpdateAvailabilityRequest
    ): AvailabilityResponse
}

data class AvailabilityResponse(
    val availability: UserAvailability
)

data class UserAvailability(
    val id: String,
    val userId: String,
    val schedule: Map<String, List<AvailabilitySlot>>,
    val updatedAt: String
)

data class AvailabilitySlot(
    val startDate: String,
    val endDate: String
)

data class UpdateAvailabilityRequest(
    val schedule: Map<String, List<AvailabilitySlot>>
)