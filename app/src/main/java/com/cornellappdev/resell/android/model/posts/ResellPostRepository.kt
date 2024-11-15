package com.cornellappdev.resell.android.model.posts

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.capitalize
import com.cornellappdev.resell.android.model.api.NewPostBody
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.PostResponse
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.util.toNetworkingString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellPostRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
) {

    private val _allPostsFlow =
        MutableStateFlow<ResellApiResponse<List<Post>>>(ResellApiResponse.Pending)
    val allPostsFlow = _allPostsFlow.asStateFlow()

    private val _savedPostsIds =
        MutableStateFlow<ResellApiResponse<List<String>>>(ResellApiResponse.Pending)

    val savedPosts = _savedPostsIds.combine(allPostsFlow) { savedIds, posts ->
        savedIds.combine(posts).map { (saved, list) ->
            list.filter {
                saved.contains(it.id)
            }
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ResellApiResponse.Pending
    )

    private var recentBitmaps: List<ImageBitmap>? = null

    /**
     * Asynchronously fetches the list of posts from the API. Once finished, will send down
     * `allPostsFlow` to be observed.
     */
    fun fetchPosts() {
        _allPostsFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _allPostsFlow.value =
                    ResellApiResponse.Success(retrofitInstance.postsApi.getPosts().posts
                        .sortedByDescending {
                            it.created
                        })
            } catch (e: Exception) {
                Log.e("ResellPostRepository", "Error fetching posts: ", e)
                _allPostsFlow.value = ResellApiResponse.Error
            }
        }
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
                categories = categories.map {
                    it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
                },
                userId = userId
            ).let {
                Log.d("helpme", it.toString())
                it
            }
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
        _savedPostsIds.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val saved = retrofitInstance.postsApi.getSavedPosts().posts

                _savedPostsIds.value = ResellApiResponse.Success(saved.map { it.id })
            } catch (e: Exception) {
                Log.e("ResellPostRepository", "Error fetching saved posts: ", e)
                _savedPostsIds.value = ResellApiResponse.Error
            }
        }
    }

    suspend fun isPostSaved(id: String): Boolean {
        return retrofitInstance.postsApi.isPostSaved(id).isSaved
    }
}
