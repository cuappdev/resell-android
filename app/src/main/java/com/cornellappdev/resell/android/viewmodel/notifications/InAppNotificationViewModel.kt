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
            when (val response = notificationRepository.getLast30DaysNotifications()) {
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
        applyMutation {
            copy(notifs = notifs.filter { it.id != notif.id })
        }
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notif.id)
            } catch (e: Exception) {
            }
        }
    }
}

enum class NotificationType {
    MESSAGE, REQUEST, DISCOUNT, BOOKMARKS, OFFER, SOLD, OTHER
}