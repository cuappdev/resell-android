package com.cornellappdev.resell.android.model.posts

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.cornellappdev.resell.android.model.api.NewPostBody
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.PostResponse
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellPostRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val userInfoRepository: UserInfoRepository
) {

    private val _allPostsFlow =
        MutableStateFlow<ResellApiResponse<List<Post>>>(ResellApiResponse.Pending)
    val allPostsFlow = _allPostsFlow.asStateFlow()

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
        val bitmaps = images.map { it.asAndroidBitmap() }

        val base64s = bitmaps.map { bitmap ->
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        return retrofitInstance.postsApi.createPost(
            NewPostBody(
                title = title,
                description = description,
                imagesBase64 = base64s,
                price = originalPrice,
                originalPrice = originalPrice,
                categories = categories.map {
                    it.uppercase()
                },
                userId = userId
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
}
