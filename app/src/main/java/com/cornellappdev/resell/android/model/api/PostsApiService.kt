package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.Listing
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date
import java.util.Locale

interface PostsApiService {
    @GET("post")
    suspend fun getPosts(): PostsResponse

    @GET("post/similar/postId/{id}")
    suspend fun getSimilarPosts(@Path("id") id: String): PostsResponse

    @POST("post/filter")
    suspend fun getFilteredPosts(@Body categoryRequest: CategoryRequest): PostsResponse
}

data class CategoryRequest(
    val category: String
)


data class PostsResponse(
    val posts: List<Post>
)

data class Post(
    val id: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    val archive: Boolean,
    val created: Date,  // Use Long for timestamps
    val price: Double,
    val images: List<String>,
    val location: String,
    val user: User // Reusing the User class from before
) {

    val priceString
        get() = String.format(Locale.US, "$%.2f", price)

    fun toListing(): Listing {
        return Listing(
            id = id,
            title = title,
            images = images,
            price = priceString,
            categories = categories,
            description = description
        )
    }
}
