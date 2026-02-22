package com.cornellappdev.resell.android.model.ptf

import com.cornellappdev.resell.android.model.api.FeedbackBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun createFeedback(uid: String, description: String) {
        retrofitInstance.feedbackApi.createFeedback(
            FeedbackBody(
                description = description,
                userId = uid,
                images = emptyList()
            )
        )
    }
}