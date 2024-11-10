package com.cornellappdev.resell.android.model.settings

import com.cornellappdev.resell.android.model.api.Report
import com.cornellappdev.resell.android.model.api.ReportBody
import com.cornellappdev.resell.android.model.api.ReportProfileBody
import com.cornellappdev.resell.android.model.api.Reporter
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val userInfoRepository: UserInfoRepository
) {

    suspend fun reportPost(id: String, reason: String) {
        // TODO: The backend input is really weird...
        retrofitInstance.reportApi.reportPost(
            ReportBody(
                report = Report(
                    id = id,
                    reason = reason,
                    type = "POST",
                    resolved = false,
                    reporter = Reporter(
                        id = userInfoRepository.getUserId()!!,
                        firstName = userInfoRepository.getFirstName()!!,
                        lastName = userInfoRepository.getLastName()!!,
                        profilePicUrl = userInfoRepository.getProfilePicUrl()!!,
                    )
                )
            )
        )
    }

    suspend fun reportProfile(uid: String, reason: String, description: String) {
        retrofitInstance.reportApi.reportProfile(
            ReportProfileBody(
                profileId = uid,
                reason = reason,
                description = description
            )
        )
    }

    suspend fun reportMessage(id: String, reason: String) {
        // TODO: The backend input is really weird...
        retrofitInstance.reportApi.reportPost(
            ReportBody(
                report = Report(
                    id = id,
                    reason = reason,
                    type = "MESSAGE",
                    resolved = false,
                    reporter = Reporter(
                        id = userInfoRepository.getUserId()!!,
                        firstName = userInfoRepository.getFirstName()!!,
                        lastName = userInfoRepository.getLastName()!!,
                        profilePicUrl = userInfoRepository.getProfilePicUrl()!!,
                    )
                )
            )
        )
    }
}
