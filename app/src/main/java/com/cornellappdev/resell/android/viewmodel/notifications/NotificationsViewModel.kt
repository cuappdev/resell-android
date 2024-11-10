package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.messages.Notification
import com.cornellappdev.resell.android.model.messages.NotificationType
import com.cornellappdev.resell.android.util.richieNotifications
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class NotificationsHubViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
) :
    ResellViewModel<NotificationsHubViewModel.NotificationsHubUiState>(
        initialUiState = NotificationsHubUiState(
            notifications = richieNotifications(40),
            loadedState = ResellApiState.Success,
            notificationType = null,
        )
    ) {

    init {
        applyMutation {
            copy(notifications = notifications.sortedBy { -it.timestate })
        }
    }

    data class NotificationsHubUiState(
        val loadedState: ResellApiState,
        val notifications: List<Notification>,
        val notificationType: NotificationType?,
    ) {
        private fun dayDifference(a: Long): Int {
            return ((LocalDateTime.now().toInstant(ZoneOffset.UTC)
                .toEpochMilli() - a) / 86400000).toInt()
        }

        val categorizedNotifications
            get() = notifications
                .filter { it.notificationType.contains(notificationType) || notificationType == null }
                .groupBy { notification ->
                    when {
                        notification.unread -> "new"
                        dayDifference(notification.timestate) <= 7 -> "week"
                        dayDifference(notification.timestate) in 8..30 -> "month"
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

    fun onToggleFilter(filter: NotificationType?) {
        applyMutation {
            copy(notificationType = filter)
        }
    }

    // TODO: Connect to corresponding page
    fun onNotificationPressed() {

    }

    // TODO: Networking
    fun onNotificationArchived(notification: Notification) {
        applyMutation {
            // Map through notifications and mark the specified notification as unread
            copy(notifications = notifications.map {
                if (it.id == notification.id) {
                    it.copy(unread = false) // Assuming you have an 'unread' field
                } else {
                    it
                }
            })
        }
        Log.d(
            "ARCHIVED NOTIFICATION",
            "Notification with the id ${notification.id} has been archived.\n" +
                    "New size: ${stateValue().unreadNotifications.size}"
        )
    }

    fun onBackPressed(){
        navController.popBackStack()
    }
}
