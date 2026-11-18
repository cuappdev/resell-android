package com.cornellappdev.resell.android.viewmodel.notifications

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.notifications.NotificationRepository
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class InAppNotificationViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val notificationRepository: NotificationRepository,
) : ResellViewModel<InAppNotificationViewModel.UiState>(
    initialUiState = UiState(
        loadedState = ResellApiState.Loading,
        notifType = null,
        notifs = emptyList()
    )
) {
    data class UiState(
        val loadedState: ResellApiState,
        val notifType: NotificationType?,
        val notifs: List<InAppNotification>,
    ) {
        val filteredNotifications: List<InAppNotification>
            get() = if (notifType == null) {
                notifs
            } else {
                notifs.filter { it.notificationType == notifType }
            }

        val categorizedNotifications: Map<String, List<InAppNotification>>
            get() = filteredNotifications.groupBy { notification ->
                val now = Instant.now()
                val days = ChronoUnit.DAYS.between(notification.createdInstant, now)
                when {
                    notification.unread -> "new"
                    days in 0..6 -> "week"
                    days in 7..29 -> "month"
                    else -> "other"
                }
            }

        val unreadNotifications: List<InAppNotification>
            get() = categorizedNotifications["new"].orEmpty()

        val weekNotifications: List<InAppNotification>
            get() = categorizedNotifications["week"].orEmpty()

        val monthNotifications: List<InAppNotification>
            get() = categorizedNotifications["month"].orEmpty()

        val otherNotifications: List<InAppNotification>
            get() = categorizedNotifications["other"].orEmpty()
    }

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            applyMutation { copy(loadedState = ResellApiState.Loading) }
            val newResponse = notificationRepository.getNewNotifications()
            val thirtyDaysResponse = notificationRepository.getLast30DaysNotifications()

            val newNotifs = when (newResponse) {
                is ResellApiResponse.Success -> newResponse.data
                else -> emptyList()
            }
            val thirtyDaysNotifs = when (thirtyDaysResponse) {
                is ResellApiResponse.Success -> thirtyDaysResponse.data
                else -> emptyList()
            }

            val combined = (newNotifs + thirtyDaysNotifs).distinctBy {
                if (it.id.isNotBlank()) it.id else "${it.title}_${it.body}"
            }

            applyMutation {
                copy(
                    loadedState = ResellApiState.Success,
                    notifs = combined
                )
            }
        }
    }

    fun onToggleFilter(filter: NotificationType?) {
        applyMutation {
            copy(notifType = if (notifType == filter) null else filter)
        }
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onNotificationPressed(notif: InAppNotification) {
        applyMutation {
            copy(
                notifs = notifs.map {
                    if (it.id == notif.id) it.copy(read = true) else it
                }
            )
        }
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notif.id)
            } catch (e: Exception) {
            }
        }
    }

    fun onNotificationArchived(notif: InAppNotification) {
        Log.d("InAppNotificationViewModel", "Archiving notification id: '${notif.id}', title: '${notif.title}'")
        applyMutation {
            copy(
                notifs = notifs.filter {
                    if (notif.id.isNotBlank()) it.id != notif.id
                    else it.title != notif.title || it.body != notif.body
                }
            )
        }
        viewModelScope.launch {
            if (notif.id.isNotBlank()) {
                try {
                    if (!notif.read) {
                        try {
                            notificationRepository.markAsRead(notif.id)
                        } catch (e: Exception) {
                            Log.e("InAppNotificationViewModel", "Failed to mark unread notification as read before deleting", e)
                        }
                    }
                    val result = notificationRepository.deleteNotification(notif.id)
                    if (result is ResellApiResponse.Error) {
                        Log.e("InAppNotificationViewModel", "Failed to delete notification ${notif.id} on backend`")
                    } else {
                        Log.d("InAppNotificationViewModel", "Successfully deleted notification ${notif.id} on backend")
                    }
                } catch (e: Exception) {
                    Log.e("InAppNotificationViewModel", "Exception deleting notification ${notif.id}", e)
                }
            } else {
                Log.e("InAppNotificationViewModel", "Notification id is blank, cannot delete on backend.")
            }
        }
    }
}

enum class NotificationType {
    MESSAGE, BOOKMARKS, OFFER, SOLD, OTHER
}