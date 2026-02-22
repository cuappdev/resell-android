package com.cornellappdev.resell.android.model.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionApiService {
    @GET("transaction/id/{id}")
    suspend fun getTransactionById(@Path("id") id : String) : TransactionResponse

    @GET("transaction/postId/{id}/")
    suspend fun getTransactionByPostId(@Path("id") postId: String): TransactionResponse

    @GET("transaction/buyerId/{id}/")
    suspend fun getTransactionsByBuyerId(@Path("id") buyerId: String): TransactionResponse

    @GET("transaction/sellerId/{id}/")
    suspend fun getTransactionsBySellerId(@Path("id") sellerId: String): TransactionResponse

    @POST("transaction")
    suspend fun createTransaction(@Body request: CreateTransactionRequest) : TransactionResponse
}

data class TransactionResponse (
    val transaction: Transaction
)

data class CreateTransactionRequest (
    val location: String,
    val amount: Float,
    val transactionDate: String?,
    val postId: String,
    val buyerId: String,
    val sellerId: String
)

@Serializable
data class Transaction(
    val id: String,
    val location: String,
    val amount: Float,
    val transactionDate: String?,
    val completed: Boolean,
    val post: Post,
    val buyer: User?,
    val seller: User?,
    val createdAt: String
)