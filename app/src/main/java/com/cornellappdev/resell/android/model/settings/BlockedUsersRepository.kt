package com.cornellappdev.resell.android.model.settings

import android.util.Log
import com.cornellappdev.resell.android.model.api.BlockBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.UnblockBody
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
class BlockedUsersRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val userInfoRepository: UserInfoRepository,
) {

    private val _blockedUsers: MutableStateFlow<ResellApiResponse<List<UserInfo>>> =
        MutableStateFlow(ResellApiResponse.Pending)
    val blockedUsers = _blockedUsers.asStateFlow()

    fun fetchBlockedUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _blockedUsers.value = ResellApiResponse.Pending
            try {
                val users =
                    retrofitInstance.userApi.getBlockedUsers(userInfoRepository.getUserId()!!)
                _blockedUsers.value = ResellApiResponse.Success(users.users.map {
                    it.toUserInfo()
                })
            } catch (e: Exception) {
                Log.e("BlockedUsersRepository", "Error fetching blocked users: ", e)
                _blockedUsers.value = ResellApiResponse.Error
            }
        }
    }

    fun onBlockUser(userId: String, onError: () -> Unit, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                retrofitInstance.userApi.blockUser(
                    BlockBody(userId)
                )

                onSuccess()
            } catch (e: Exception) {
                Log.e("BlockedUsersRepository", "Error blocking user: ", e)
                onError()
            }
        }
    }

    fun onUnblockUser(userId: String, onError: () -> Unit, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                retrofitInstance.userApi.unblockUser(
                    UnblockBody(userId)
                )

                onSuccess()
            } catch (e: Exception) {
                Log.e("BlockedUsersRepository", "Error blocking user: ", e)
                onError()
            }
        }
    }
}
