package com.cornellappdev.resell.android.model.notifications

import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.DiscountNotifBody
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ListingRequestNotifBody
import com.cornellappdev.resell.android.model.classes.RecentNotifResponse
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppNotifRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
) {

    val api = retrofitInstance.inAppNotifApi
    private val _recentNotifs = MutableStateFlow<ResellApiResponse<List<RecentNotifResponse>>>(
        ResellApiResponse.Pending
    )
    val recentNotifs = _recentNotifs.asStateFlow()

    suspend fun getRecentNotifs() {
        _recentNotifs.value = ResellApiResponse.Pending
        runCatching {
            val notifs = retrofitInstance.inAppNotifApi.getRecentNotifs()
            _recentNotifs.value = ResellApiResponse.Success(notifs)
        }.getOrElse { e ->
            _recentNotifs.value = ResellApiResponse.Error
        }
    }

    suspend fun onNotificationArchived(notif: InAppNotification) {
        //TODO: implement when there an endpoint to archive a notification
    }

    //TODO: when should this be called
    suspend fun sendRequestMatchNotif(body: ListingRequestNotifBody) {
        retrofitInstance.inAppNotifApi.sendRequestMatchNotif(body)
    }

    //TODO: will be called when post price is changed, which doesn't have an implementation yet
    suspend fun sendDiscountNotification(body: DiscountNotifBody) {
        retrofitInstance.inAppNotifApi.sendDiscountNotification(body)
    }
}



