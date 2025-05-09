package com.cornellappdev.resell.android.model.settings

import com.cornellappdev.resell.android.model.api.FeedbackBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun sendFeedback(uid: String, content: String) {
        retrofitInstance.feedbackApi.sendFeedback(
            FeedbackBody(
                userId = uid,
                content = content
            )
        )
    }
}