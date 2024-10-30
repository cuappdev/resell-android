package com.cornellappdev.resell.android.model.profile

import android.util.Log
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
) {

    private val _internalUser: MutableStateFlow<ResellApiResponse<UserInfo>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val internalUser = _internalUser.asStateFlow()

    private val _internalListings: MutableStateFlow<ResellApiResponse<List<Listing>>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val internalListings = _internalListings.asStateFlow()

    private val _internalArchivedListings: MutableStateFlow<ResellApiResponse<List<Listing>>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val internalArchivedListings = _internalArchivedListings.asStateFlow()

    /**
     * Initiates a fetch of the user's internal profile from the API.
     *
     * Pipelines the response into [internalUser].
     */
    fun fetchInternalProfile(id: String) {
        _internalUser.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.userApi.getUser(id)
                _internalUser.value = ResellApiResponse.Success(response.user.toUserInfo())
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching profile: ", e)
                _internalUser.value = ResellApiResponse.Error
            }
        }
    }

    /**
     * Initiates a fetch of the user's internal listings from the API.
     *
     * Pipelines the response into [internalListings].
     */
    fun fetchInternalListings(id: String) {
        _internalListings.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.postsApi.getPostsByUser(id)
                _internalListings.value = ResellApiResponse.Success(response.posts.map {
                    it.toListing()
                })
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching listings: ", e)
                _internalListings.value = ResellApiResponse.Error
            }
        }
    }

    fun fetchArchivedListings(id: String) {
        _internalArchivedListings.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.postsApi.getArchivedPostsByUser(id)
                _internalArchivedListings.value = ResellApiResponse.Success(response.posts.map {
                    it.toListing()
                })
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching listings: ", e)
                _internalArchivedListings.value = ResellApiResponse.Error
            }
        }
    }
}
