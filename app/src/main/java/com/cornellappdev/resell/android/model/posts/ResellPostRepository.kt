package com.cornellappdev.resell.android.model.posts

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.model.api.FilterRequest
import com.cornellappdev.resell.android.model.api.NewPostBody
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.PostResponse
import com.cornellappdev.resell.android.model.api.PriceRange
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.FilterCondition
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.classes.SortBy
import com.cornellappdev.resell.android.util.toNetworkingString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellPostRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
) {

    private val _savedPosts =
        MutableStateFlow<ResellApiResponse<List<Post>>>(ResellApiResponse.Pending)
    val savedPosts = _savedPosts.asStateFlow()

    private var recentBitmaps: List<ImageBitmap>? = null

    suspend fun getPostsByPage(page: Int): List<Post> {
        val posts = retrofitInstance.postsApi.getPosts(page = page).posts
        return posts
    }

    suspend fun getFilteredPosts(filter: ResellFilter): List<Post> {
        val lowerPrice = filter.priceRange.first
        val higherPrice = filter.priceRange.last
        val categories = filter.categoriesSelected
        val condition = filter.conditionSelected
        val sortBy = filter.sortBy
        return retrofitInstance.postsApi.getFilteredPosts(
            FilterRequest(
                price = PriceRange(
                    lowerBound = lowerPrice.toDouble(),
                    upperBound = higherPrice.toDouble()
                ),
                condition = when (condition) {
                    FilterCondition.GENTLY_USED -> "gentlyUsed"
                    FilterCondition.NEVER_USED -> "new"
                    FilterCondition.WORN -> "worn"
                    null -> null
                },
                categories = if (categories.isEmpty()) null else categories.map {
                    it.label
                },
                sortField = when (sortBy) {
                    SortBy.ANY -> "any"
                    SortBy.PRICE_LOW_TO_HIGH -> "priceLowToHigh"
                    SortBy.PRICE_HIGH_TO_LOW -> "priceHighToLow"
                    SortBy.NEWLY_LISTED -> "newlyListed"
                }
            )
        ).posts
        // TODO: need a route for items on sale
    }


    suspend fun uploadPost(
        title: String,
        description: String,
        images: List<ImageBitmap>,
        originalPrice: Double,
        categories: List<String>,
        userId: String,
    ): PostResponse {
        val base64s = images.map { it.toNetworkingString() }

        return retrofitInstance.postsApi.createPost(
            NewPostBody(
                title = title,
                description = description,
                imagesBase64 = base64s,
                originalPrice = originalPrice,
                categories = categories.map { category ->
                    category.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
                    }
                },
                userId = userId,
                // TODO: New designs incoming to allow a change here?
                condition = "NEW"
            )
        )
    }

    fun cacheImageBitmaps(bitmaps: List<ImageBitmap>) {
        recentBitmaps = bitmaps
    }

    fun getRecentBitmaps(): List<ImageBitmap>? {
        return recentBitmaps
    }

    suspend fun deletePost(id: String): PostResponse {
        return retrofitInstance.postsApi.deletePost(id)
    }

    suspend fun savePost(id: String): PostResponse {
        return retrofitInstance.postsApi.savePost(id).apply {
            fetchSavedPosts()
        }
    }

    suspend fun unsavePost(id: String): PostResponse {
        return retrofitInstance.postsApi.unsavePost(id).apply {
            fetchSavedPosts()
        }
    }

    suspend fun archivePost(id: String): PostResponse {
        return retrofitInstance.postsApi.archivePost(id)
    }

    /**
     * Starts a coroutine to fetch the list of saved posts from the API.
     *
     * Sometime after calling, [savedPosts] will be updated.
     */
    fun fetchSavedPosts() {
        _savedPosts.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val saved = retrofitInstance.postsApi.getSavedPosts().posts

                _savedPosts.value = ResellApiResponse.Success(saved)
            } catch (e: Exception) {
                Log.e("ResellPostRepository", "Error fetching saved posts: ", e)
                _savedPosts.value = ResellApiResponse.Error
            }
        }
    }

    suspend fun isPostSaved(id: String): Boolean {
        return retrofitInstance.postsApi.isPostSaved(id).isSaved
    }

    suspend fun getPostById(id: String): Post {
        return retrofitInstance.postsApi.getPost(id).post
    }
}
