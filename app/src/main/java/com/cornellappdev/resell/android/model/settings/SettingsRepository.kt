package com.cornellappdev.resell.android.model.settings

import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.model.api.EditUser
import com.cornellappdev.resell.android.model.api.Feedback
import com.cornellappdev.resell.android.model.api.ReportPostBody
import com.cornellappdev.resell.android.model.api.ReportProfileBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.util.toNetworkingString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val userInfoRepository: UserInfoRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val profileRepository: ProfileRepository
) {

    suspend fun reportPost(id: String, uid: String, reason: String) {
        // TODO: The backend input is really weird...
        retrofitInstance.settingsApi.reportPost(
            ReportPostBody(
                reported = uid,
                post = id,
                reason = reason,
            )
        )
    }

    suspend fun reportProfile(uid: String, reason: String, description: String) {
        retrofitInstance.settingsApi.reportProfile(
            ReportProfileBody(
                reported = uid,
                reason = reason,
            )
        )
    }

    suspend fun reportMessage(id: String, reason: String) {
        // TODO: The backend input is really weird...
    }

    suspend fun sendFeedback(
        description: String,
        images: List<ImageBitmap>,
    ) {
        retrofitInstance.settingsApi.sendFeedback(
            Feedback(
                description = description,
                imagesBase64 = images.map { it.toNetworkingString() },
                userId = userInfoRepository.getUserId()!!
            )
        )
    }

    suspend fun editProfile(
        username: String,
        venmo: String,
        bio: String,
        image: ImageBitmap?
    ) {
        retrofitInstance.settingsApi.editUser(
            EditUser(
                username = username,
                venmoHandle = venmo,
                bio = bio,
                profilePicBase64 = image?.toNetworkingString() ?: ""
            )
        )

        fireStoreRepository.saveVenmo(
            userInfoRepository.getEmail()!!,
            venmo
        )

        profileRepository.fetchInternalProfile(userInfoRepository.getUserId()!!)
    }
}
