package com.cornellappdev.resell.android.model.posts

import android.util.Log
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    /**
     * Asynchronously fetches the list of posts from the API. Once finished, will send down
     * `allPostsFlow` to be observed.
     */
    fun fetchPosts() {
        _allPostsFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _allPostsFlow.value =
                    ResellApiResponse.Success(retrofitInstance.postsApi.getPosts().posts)
                Log.d("helpme", "success")
            } catch (e: Exception) {
                Log.e("ResellPostRepository", "Error fetching posts: ", e)
                _allPostsFlow.value = ResellApiResponse.Error
            }
        }
    }
}
