package com.cornellappdev.resell.android.viewmodel.notifications

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toInAppNotif
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.notifications.InAppNotifRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class InAppNotificationViewModel @Inject constructor(
    private val inAppNotifRepository: InAppNotifRepository,
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
        viewModelScope.launch {
            inAppNotifRepository.getRecentNotifs()
        }
        asyncCollect(inAppNotifRepository.recentNotifs) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    notifs = response.asSuccessOrNull()?.data?.map { it.toInAppNotif() }
                        ?: emptyList()
                )
            }
        }
    }

    fun onToggleFilter(filter: NotificationType?) {
        applyMutation {
            copy(
                loadedState = ResellApiState.Loading
            )
        }
        if (filter == null) {
            asyncCollect(inAppNotifRepository.recentNotifs) { response ->
                applyMutation {
                    copy(
                        loadedState = response.toResellApiState(),
                        notifs = response.asSuccessOrNull()?.data?.map { it.toInAppNotif() }
                            ?: emptyList(),
                        notifType = null
                    )
                }
            }
        } else {
            asyncCollect(inAppNotifRepository.recentNotifs) { response ->
                applyMutation {
                    copy(
                        loadedState = response.toResellApiState(),
                        notifs = response.asSuccessOrNull()?.data?.map { it.toInAppNotif() }
                            ?.filter { it.notificationType == filter }
                            ?: emptyList(),
                        notifType = filter
                    )
                }
            }
        }
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onNotificationPressed(notif: InAppNotification) {
        viewModelScope.launch {
            runCatching {
                if (notif.data.postId != null) {
                    val post = resellPostRepository.getPostById(notif.data.postId)
                    rootNavigationRepository.navigateToPdp(post.toListing())
                }
            }.getOrElse { e ->
                Log.e("ResellPostRepository", "Error navigating to post ", e)

            }
        }
    }

    fun onNotificationArchived(notif: InAppNotification) {
        viewModelScope.launch {
            inAppNotifRepository.onNotificationArchived(notif)
        }
    }
}

enum class NotificationType {
    MESSAGE, REQUEST, DISCOUNT, BOOKMARKS, OFFER, SOLD, OTHER
}
