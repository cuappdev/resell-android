package com.cornellappdev.resell.android.model.profile

import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.SearchRequest
import com.cornellappdev.resell.android.model.classes.Listing
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {

    // TODO need to search within category
    suspend fun searchPostByUser(uid: String?, keywords: String): List<Listing> {
        // TODO Backend should improve this and make a new endpoint just for this.
        val search = retrofitInstance.postsApi.getPostsBySearch(
            SearchRequest(
                keywords = keywords
            )
        )
        return search.posts.map { it.toListing() }.filter {
            it.user.id == uid || uid == null
        }
    }
}
