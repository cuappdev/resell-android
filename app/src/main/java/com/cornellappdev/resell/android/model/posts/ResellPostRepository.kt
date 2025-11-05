package com.cornellappdev.resell.android.model.posts

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.api.NewPostBody
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.PostResponse
import com.cornellappdev.resell.android.model.api.ResellSearchHistory
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.classes.toFilterRequest
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import com.cornellappdev.resell.android.util.toNetworkingString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellPostRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val dataStore: DataStore<Preferences>,
) {

    private val _savedPosts =
        MutableStateFlow<ResellApiResponse<List<Post>>>(ResellApiResponse.Pending)
    val savedPosts = _savedPosts.asStateFlow()

    private val _fromSearchedPosts =
        MutableStateFlow<ResellApiResponse<List<Pair<String, List<Listing>>>>>(ResellApiResponse.Pending)
    val fromSearchedPosts = _fromSearchedPosts.asStateFlow()

    private val _fromPurchasedPosts =
        MutableStateFlow<ResellApiResponse<List<Listing>>>(ResellApiResponse.Pending)
    val fromPurchasedPosts = _fromPurchasedPosts.asStateFlow()

    private val _searchHistory = MutableStateFlow<ResellApiResponse<List<ResellSearchHistory>>>(
        ResellApiResponse.Pending
    )

    private var recentBitmaps: List<ImageBitmap>? = null

    suspend fun getPostsByPage(page: Int): List<Post> {
        val posts = retrofitInstance.postsApi.getPosts(page = page).posts
        return posts
    }

    suspend fun getFilteredPosts(filter: ResellFilter): List<Post> {
        return retrofitInstance.postsApi.getFilteredPosts(filter.toFilterRequest()).posts

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

    /**
     * Given user's search history, filters out item is HiddenSearches and gets suggestions for each
     * search in search history
     * @param history - User's search history
     */
    suspend fun fetchPostsFromSearch(history: List<ResellSearchHistory>) {
        _fromSearchedPosts.value = ResellApiResponse.Pending
        val hiddenSearchesJson = dataStore.data.map { preferences ->
            preferences[PreferencesKeys.HIDDEN_SEARCHES]
        }.firstOrNull()
        val hiddenSearches = runCatching {
            if (!hiddenSearchesJson.isNullOrBlank()) {
                val jsonArray = org.json.JSONArray(hiddenSearchesJson)
                MutableList(jsonArray.length()) { i -> jsonArray.optString(i) }.toMutableList()
            } else {
                mutableListOf()
            }
        }.getOrElse { e ->
            Log.e("ResellPostRepository", "Error fetching search hiddenSearches ", e)
            mutableListOf()
        }

        CoroutineScope(Dispatchers.IO).launch {
            //if (history.isEmpty()) return@launch
            history.forEach { userSearch ->
                if (userSearch.id.isBlank() || hiddenSearches.contains(userSearch.searchText)) return@forEach
                runCatching {
                    val fromSearched =
                        retrofitInstance.postsApi.getSearchSuggestions(id = userSearch.id)

                    val posts = fromSearched.postIds.map { id ->
                        async {
                            runCatching { getPostById(id).toListing() }.getOrNull()
                        }
                    }.awaitAll().filterNotNull()
                    _fromSearchedPosts.update { current ->
                        if (current is ResellApiResponse.Success<List<Pair<String, List<Listing>>>>) {
                            ResellApiResponse.Success(current.data + listOf(userSearch.searchText to posts))
                        } else {
                            ResellApiResponse.Success(listOf(userSearch.searchText to posts))
                        }
                    }
                }
            }
        }
    }

    fun fetchPostsFromPurchase() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val fromPurchases = retrofitInstance.postsApi.getPurchaseSuggestions()
                val posts = fromPurchases.postIds.map { id ->
                    async {
                        runCatching { getPostById(id).toListing() }
                            .getOrNull()
                    }
                }.awaitAll().filterNotNull()
                Log.d("helpme", posts.toString())
                _fromPurchasedPosts.value = ResellApiResponse.Success(posts)
            }
        }
    }

    fun getSearchHistory() {
        _searchHistory.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val history = retrofitInstance.searchApi.getSearchHistory()
                _searchHistory.value = ResellApiResponse.Success(history.searches)
                fetchPostsFromSearch(history.searches)
            }.getOrElse { e ->
                Log.e("ResellPostRepository", "Error fetching search history: ", e)
                _searchHistory.value = ResellApiResponse.Error
            }
        }
    }

    /**
     * Adds the search to the hiddenSearches list: a list of searches of who's suggestions will not
     * be displayed
     *
     * @param search - hidden search text
     * **/
    fun editHiddenSearches(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentJson = dataStore.data.map { preferences ->
                preferences[PreferencesKeys.HIDDEN_SEARCHES]
            }.firstOrNull()

            val currentList = runCatching {
                if (!currentJson.isNullOrBlank()) {
                    val jsonArray = org.json.JSONArray(currentJson)
                    MutableList(jsonArray.length()) { i -> jsonArray.optString(i) }.toMutableList()
                } else {
                    mutableListOf<String>()
                }
            }.getOrElse { e ->
                Log.e("ResellPostRepository", "Error getting hidden searches: ", e)
                mutableListOf()
            }

            currentList.add(search)
            val newJson = org.json.JSONArray(currentList).toString()

            runCatching {
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.HIDDEN_SEARCHES] = newJson
                }
            }.onSuccess {
                _fromSearchedPosts.update { current ->
                    if (current is ResellApiResponse.Success<List<Pair<String, List<Listing>>>>) {
                        ResellApiResponse.Success(current.data.filter { it.first != search })
                    } else {
                        current
                    }
                }
            }.getOrElse { e ->
                Log.e("ResellPostRepository", "Error editing hidden searches: ", e)
            }
        }
    }

}
