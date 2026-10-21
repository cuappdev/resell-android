package com.cornellappdev.resell.android.viewmodel.notifications

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
import javax.inject.Inject

@HiltViewModel
class InAppNotificationViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val notificationRepository: NotificationRepository,
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
            return try {
                val instant = Instant.parse(iso)
                ((Instant.now().toEpochMilli() - instant.toEpochMilli()) / 86_400_000).toInt()
            } catch (e: Exception) {
                0
            }
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
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            applyMutation { copy(loadedState = ResellApiState.Loading) }
            when (val response = notificationRepository.getNewNotifications()) {
                is ResellApiResponse.Success -> {
                    applyMutation {
                        copy(
                            loadedState = ResellApiState.Success,
                            notifs = response.data
                        )
                    }
                }

                else -> {
                    applyMutation {
                        copy(
                            loadedState = ResellApiState.Success,
                            notifs = emptyList()
                        )
                    }
                }
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
                    if (it.id == notif.id) it.copy(unread = false) else it
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
        applyMutation {
            copy(
                notifs = notifs.filter { it.id != notif.id }
            )
        }
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notif.id)
            } catch (e: Exception) {
                // Keep local optimistic deletion
            }
        }
    }
}

enum class NotificationType {
    MESSAGE, REQUEST, DISCOUNT, BOOKMARKS, OFFER, SOLD, OTHER
}
