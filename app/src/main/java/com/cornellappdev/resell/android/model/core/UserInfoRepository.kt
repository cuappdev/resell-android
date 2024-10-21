package com.cornellappdev.resell.android.model.core

import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.util.richieUserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepository @Inject constructor() {

    private val userInfo = richieUserInfo

    /**
     * If the user is signed in, returns the user's information. Otherwise, throws an exception.
     *
     * Requires the user to be signed in.
     */
    suspend fun getUserInfo(): UserInfo {
        return userInfo
    }
}
