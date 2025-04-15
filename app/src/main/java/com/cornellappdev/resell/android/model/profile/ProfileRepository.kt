package com.cornellappdev.resell.android.model.profile

import android.util.Log
import com.cornellappdev.resell.android.model.api.PostRequestBody
import com.cornellappdev.resell.android.model.api.RequestResponse
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.UserResponse
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.RequestListing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.model.core.UserInfoRepository
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
    private val userInfoRepository: UserInfoRepository
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

    private val _externalUser: MutableStateFlow<ResellApiResponse<UserInfo>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val externalUser = _externalUser.asStateFlow()

    private val _externalListings: MutableStateFlow<ResellApiResponse<List<Listing>>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val externalListings = _externalListings.asStateFlow()

    // Requests.

    private val _requests: MutableStateFlow<ResellApiResponse<List<RequestListing>>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val requests = _requests.asStateFlow()

    /**
     * Fetches the requests made by the user.
     *
     * Pipelines the response into [requests].
     */
    fun fetchRequests(uid: String) {
        _requests.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.requestsApi.getRequestsByUser(uid)
                _requests.value = ResellApiResponse.Success(
                    data = response.requests.map {
                        it.toRequestListing()
                    })
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching requests: ", e)
                _requests.value = ResellApiResponse.Error
            }
        }
    }

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

    fun fetchExternalListings(id: String) {
        _externalListings.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.postsApi.getPostsByUser(id)
                _externalListings.value = ResellApiResponse.Success(response.posts.map {
                    it.toListing()
                })
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching listings: ", e)
                _externalListings.value = ResellApiResponse.Error
            }
        }
    }

    fun fetchExternalProfile(id: String) {
        _externalUser.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofitInstance.userApi.getUser(id)
                _externalUser.value = ResellApiResponse.Success(response.user.toUserInfo())
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching profile: ", e)
                _externalUser.value = ResellApiResponse.Error
            }
        }
    }

    suspend fun createRequestListing(
        title: String,
        description: String,
        userId: String
    ): RequestResponse {
        return retrofitInstance.requestsApi.createRequest(
            request = PostRequestBody(
                title = title,
                description = description,
                userId = userId
            )
        )
    }

    suspend fun deleteRequestListing(id: String): RequestResponse {
        return retrofitInstance.requestsApi.deleteRequest(id)
    }

    suspend fun getRequestById(id: String): RequestResponse {
        return retrofitInstance.requestsApi.getRequest(id)
    }

    fun softDelete(onSuccess: () -> Unit, onError: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = retrofitInstance.userApi.deleteUser()
                onSuccess()
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error soft deleting user: ", e)
                onError()
            }
        }
    }

    suspend fun getUserById(id: String): UserResponse {
        return retrofitInstance.userApi.getUser(id)
    }
}
