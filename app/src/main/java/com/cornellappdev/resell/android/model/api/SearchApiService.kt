package com.cornellappdev.resell.android.model.api

import retrofit2.http.GET

interface SearchApiService {

    @GET("search")
    suspend fun getSearchHistory(): SearchHistoryResponse
}

data class SearchHistoryResponse(
    val searches: List<ResellSearchHistory>
)

data class ResellSearchHistory(
    val id: String,
    val searchText: String,
    val createdAt: String
)