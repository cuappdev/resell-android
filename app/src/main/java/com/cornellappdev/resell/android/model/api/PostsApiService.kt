package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.Listing
import retrofit2.http.GET
import java.util.Date
import java.util.Locale

interface PostsApiService {
    @GET("post")
    suspend fun getPosts(): PostsResponse
}

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
            image = images[0],
            price = priceString,
            categories = categories
        )
    }
}
