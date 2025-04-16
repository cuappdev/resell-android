package com.cornellappdev.resell.android.model.api

import com.google.firebase.Timestamp
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {
    @POST("chat/{chatId}")
    suspend fun sendChat(
        @Body chatBody: ChatBody,
        @Path("chatId") chatId: String
    )

    @POST("chat/{chatId}")
    suspend fun sendAvailability(
        @Body availabilityBody: AvailabilityBody,
        @Path("chatId") chatId: String
    )

    @POST("chat/{chatId}")
    suspend fun sendProposal(
        @Body proposalBody: ProposalBody,
        @Path("chatId") chatId: String
    )

    @POST("chat/{chatId}")
    suspend fun sendProposalResponse(
        @Body proposalResponseBody: ProposalResponseBody,
        @Path("chatId") chatId: String
    )

    @POST("chat/{chatId}/message/{messageId}")
    suspend fun markChatRead(
        @Body markReadBody: MarkReadBody,
        @Path("chatId") chatId: String,
        @Path("messageId") messageId: String,
    )
}

data class ChatBody(
    val type: String = "chat",
    val listingId: String,
    val buyerId: String,
    val sellerId: String,
    val senderId: String,
    val text: String,
    val images: List<String>
)

data class AvailabilityBody(
    val type: String = "availability",
    val listingId: String,
    val buyerId: String,
    val sellerId: String,
    val senderId: String,
    val availabilities: List<StartAndEnd>
)

data class StartAndEnd(
    val startDate: Timestamp,
    val endDate: Timestamp
)

data class ProposalBody(
    val type: String = "proposal",
    val listingId: String,
    val buyerId: String,
    val sellerId: String,
    val senderId: String,
    val startDate: Timestamp,
    val endDate: Timestamp
)

data class ProposalResponseBody(
    val type: String = "proposal",
    val listingId: String,
    val buyerId: String,
    val sellerId: String,
    val senderId: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
    val accepted: Boolean
)

data class MarkReadBody(
    val read: Boolean
)
