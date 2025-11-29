package com.cornellappdev.resell.android.model.notifications

import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.DiscountNotificationBody
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.RecentNotifResponse
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppNotificationRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
) {

    private val _recentNotifications =
        MutableStateFlow<ResellApiResponse<List<RecentNotifResponse>>>(
            ResellApiResponse.Pending
        )
    val recentNotifs = _recentNotifications.asStateFlow()

    suspend fun getRecentNotifications() {
        _recentNotifications.value = ResellApiResponse.Pending
        runCatching {
            val notifs = retrofitInstance.inAppNotificationApi.getRecentNotifications()
            _recentNotifications.value = ResellApiResponse.Success(notifs)
        }.getOrElse { _ ->
            _recentNotifications.value = ResellApiResponse.Error
        }
    }

    suspend fun onNotificationArchived(notif: InAppNotification) {
        //TODO: implement when there is an endpoint to archive a notification
    }

    //TODO: will be called when post price is changed, which doesn't have an implementation yet
    suspend fun sendDiscountNotification(body: DiscountNotificationBody) {
        runCatching {
            retrofitInstance.inAppNotificationApi.sendDiscountNotification(body)
        }.onFailure { e ->
            println("Error sending discount notification: ${e.message}")
        }
    }
}



