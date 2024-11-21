package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.util.richieUserInfo
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date
import java.util.Locale

interface PostsApiService {
    @GET("post")
    suspend fun getPosts(): PostsResponse

    @GET("post/id/{id}")
    suspend fun getPost(@Path("id") id: String): Post

    @GET("post/similar/postId/{id}")
    suspend fun getSimilarPosts(@Path("id") id: String): PostsResponse

    @POST("post/filter")
    suspend fun getFilteredPosts(@Body categoryRequest: CategoryRequest): PostsResponse

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

data class CategoryRequest(
    val category: String
)


data class PostsResponse(
    val posts: List<Post>
)

data class PostResponse(
    val post: Post
)

data class Post(
    val id: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    val archive: Boolean,
    val created: Date,  // Use Long for timestamps
    val price: Double,
    @SerializedName("altered_price") val altered: String,
    val images: List<String>,
    val location: String,
    val user: User? // Reusing the User class from before
) {

    private val priceString
        get() = String.format(Locale.US, "$%.2f", altered?.toDouble() ?: 0.0)

    fun toListing(): Listing {
        return Listing(
            id = id,
            title = title,
            images = images,
            price = priceString,
            categories = categories,
            description = description,
            user = user?.toUserInfo() ?: richieUserInfo.apply {
                Log.e("PostsApiService", "User is null")
            },
        )
    }
}

data class NewPostBody(
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerializedName("original_price") val originalPrice: Double,
    val imagesBase64: List<String>,
    val userId: String
)

data class IsSavedResponse(
    val isSaved: Boolean
)
