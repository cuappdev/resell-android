package com.cornellappdev.resell.android.model.api

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Date

interface TransactionApiService {
    @GET("transaction/id/{id}")
    suspend fun getTransactionById(@Path("id") id : String) : TransactionResponse

    @GET("transaction/postId/{id}/")
    suspend fun getTransactionByPostId(@Path("id") postId: String): TransactionResponse
}

data class TransactionResponse (
    val transaction: Transaction
)

@Serializable
data class Transaction(
    val id: String,
    val location: String,
    val amount: Float,
    @Contextual
    val transactionDate: Date,
    val completed: Boolean,
    val post: Post,
    val buyer: User?,
    val seller: User?,
    val createdAt: String
)