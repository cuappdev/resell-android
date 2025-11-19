package com.cornellappdev.resell.android.viewmodel.notifications

import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class InAppNotificationViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
) : ResellViewModel<InAppNotificationViewModel.UiState>(
    initialUiState = UiState(
        loadedState = ResellApiState.Loading,
        notifType = null,
        notifs = listOf()
    )
) {
    data class UiState(
        val loadedState: ResellApiState,
        val notifType: NotificationType?,
        val notifs: List<InAppNotification>,
    ) {


        private fun dayDifferenceFromIso(iso: String): Int {
            val instant = Instant.parse(iso)
            return ((Instant.now().toEpochMilli() - instant.toEpochMilli()) / 86_400_000).toInt()
        }

        val categorizedNotifications
            get() = notifs
                .filter { it.notificationType == notifType || notifType == null }
                .groupBy { notification ->
                    when {
                        notification.unread -> "new"
                        dayDifferenceFromIso(notification.timeState) <= 7 -> "week"
                        dayDifferenceFromIso(notification.timeState) in 8..30 -> "month"
                        else -> "other"
                    }
                }

        val unreadNotifications
            get() = categorizedNotifications["new"].orEmpty()
        val weekNotifications
            get() = categorizedNotifications["week"].orEmpty()
        val monthNotifications
            get() = categorizedNotifications["month"].orEmpty()
        val otherNotifications
            get() = categorizedNotifications["other"].orEmpty()

    }

    init {
        //TODO: Implement networking
    }

    fun onToggleFilter(filter: NotificationType?) {
        //TODO: Implement networking
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onNotificationPressed() {
        //TODO: Implement navigation
    }

    fun onNotificationArchived(notif: InAppNotification) {
        //TODO: Implement networking
    }
}

enum class NotificationType {
    MESSAGE, REQUEST, DISCOUNT, BOOKMARKS, OFFER, SOLD, OTHER
}
