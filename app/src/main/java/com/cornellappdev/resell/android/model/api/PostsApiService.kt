package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.util.richieUserInfo
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface PostsApiService {
    @GET("post")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") size: Int = 10
    ): PostsResponse

    @GET("post/id/{id}")
    suspend fun getPost(@Path("id") id: String): PostResponse

    @GET("post/similar/postId/{id}")
    suspend fun getSimilarPosts(@Path("id") id: String): PostsResponse

    @POST("post/filterByCategories")
    suspend fun getFilteredPosts(@Body categoriesRequest: CategoriesRequest): PostsResponse

    @GET("post/userId/{id}")
    suspend fun getPostsByUser(@Path("id") id: String): PostsResponse

    @GET("post/archive/userId/{id}")
    suspend fun getArchivedPostsByUser(@Path("id") id: String): PostsResponse

    @POST("post/search")
    suspend fun getPostsBySearch(@Body searchRequest: SearchRequest): PostsResponse

    @POST("post")
    suspend fun createPost(@Body newPostBody: NewPostBody): PostResponse

    @DELETE("post/id/{id}")
    suspend fun deletePost(@Path("id") id: String): PostResponse

    @POST("post/save/postId/{id}")
    suspend fun savePost(@Path("id") id: String): PostResponse

    @POST("post/unsave/postId/{id}")
    suspend fun unsavePost(@Path("id") id: String): PostResponse

    @POST("post/archive/postId/{id}")
    suspend fun archivePost(@Path("id") id: String): PostResponse

    @GET("post/save")
    suspend fun getSavedPosts(): PostsResponse

    @GET("post/isSaved/postId/{id}")
    suspend fun isPostSaved(@Path("id") id: String): IsSavedResponse
}

data class SearchRequest(
    val keywords: String
)

data class CategoriesRequest(
    val categories: List<String>
)


data class PostsResponse(
    val posts: List<Post>
)

data class PostResponse(
    val post: Post
)

@Serializable
data class Category(
    val id: String,
    val name: String
)

@Serializable
data class Post(
    val id: String,
    val title: String,
    val description: String,
    val categories: List<Category>,
    val archive: Boolean,
    private val created: String,
    @SerializedName("altered_price") val alteredPrice: String,
    val images: List<String>,
    val location: String?,
    val user: User? // Reusing the User class from before
) {

    private val priceString
        get() = String.format(Locale.US, "$%.2f", alteredPrice.ifBlank { null }?.toDouble() ?: 0.0)

    val createdDate: Date
        get() = parseIsoDate(created)

    fun toListing(): Listing {
        return Listing(
            id = id,
            title = title,
            images = images,
            price = priceString,
            categories = categories.map { it.name },
            description = description,
            user = user?.toUserInfo() ?: richieUserInfo.apply {
                Log.e("PostsApiService", "User is null")
            },
        )
    }
}

private fun parseIsoDate(dateString: String): Date {
    // Define the date format that matches the input string
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    // Set the timezone to UTC because ISO 8601 uses UTC time
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    // Parse the string into a Date object
    return dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
}

data class NewPostBody(
    val title: String,
    val description: String,
    val category: String,
    val condition: String,
    @SerializedName("original_price") val originalPrice: Double,
    val imagesBase64: List<String>,
    val userId: String
)

data class IsSavedResponse(
    val isSaved: Boolean
)
