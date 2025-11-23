package com.cornellappdev.resell.android.viewmodel.notifications

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toInAppNotification
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.notifications.InAppNotificationRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class InAppNotificationViewModel @Inject constructor(
    private val inAppNotificationRepository: InAppNotificationRepository,
    private val navController: RootNavigationRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository
) : ResellViewModel<InAppNotificationViewModel.UiState>(
    initialUiState = UiState(
        loadedState = ResellApiState.Loading,
        notifType = null,
        notifs = listOf()
    )
) {
    private val notificationFilter = MutableStateFlow<NotificationType?>(null)

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
                        notification.isUnread -> "new"
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
        viewModelScope.launch {
            inAppNotificationRepository.getRecentNotifications()
        }
        applyMutation {
            copy(loadedState = ResellApiState.Loading)
        }
        combine(
            inAppNotificationRepository.recentNotifs,
            notificationFilter
        ) { response, filter ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    notifs = response.asSuccessOrNull()?.data?.map { it.toInAppNotification() }
                        ?.let { notifs -> if (filter != null) notifs.filter { it.notificationType == filter } else notifs }
                        ?: emptyList(),
                    notifType = filter
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onToggleFilter(filter: NotificationType?) {
        notificationFilter.value = filter
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onNotificationPressed(notif: InAppNotification) {
        viewModelScope.launch {
            runCatching {
                notif.data.postId?.let { postId ->
                    val post = resellPostRepository.getPostById(postId)
                    rootNavigationRepository.navigateToPdp(post.toListing())
                }
            }.getOrElse { e ->
                Log.e("ResellPostRepository", "Error navigating to post ", e)

            }
        }
    }

    fun onNotificationArchived(notif: InAppNotification) = viewModelScope.launch {
        inAppNotificationRepository.onNotificationArchived(notif)
    }
}


enum class NotificationType {
    MESSAGE, REQUEST, DISCOUNT, BOOKMARKS, OFFER, SOLD, OTHER
}
