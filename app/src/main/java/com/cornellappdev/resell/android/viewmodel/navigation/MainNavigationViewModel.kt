package com.cornellappdev.resell.android.viewmodel.navigation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.NotificationData
import com.cornellappdev.resell.android.model.core.DeepLinkRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.settings.NotificationsRepository
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    notificationsRepository: NotificationsRepository,
    mainNavigationRepository: MainNavigationRepository,
    private val deepLinkRepository: DeepLinkRepository,
    private val postRepository: ResellPostRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

    companion object {
        private const val TAG = "MainNavigationViewModel"
    }

    data class MainNavigationUiState(
        val newPostExpanded: Boolean,
        val navEvent: UIEvent<ResellMainScreen>? = null,
        val bottomBarEnabled: Boolean = false,
        val notificationData: NotificationData? = null
    )

    init {
        asyncCollect(mainNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    navEvent = route
                )
            }
        }

        asyncCollect(notificationsRepository.notificationData) { event ->
            if (event != null) {
                val data = event.payload
                applyMutation {
                    copy(notificationData = data)
                }

                if (stateValue().bottomBarEnabled) {
                    parseNotification(data)
                }
            }
        }

        asyncCollect(deepLinkRepository.pendingPostId) { postId ->
            if (postId != null) {
                openSharedPost(postId)
            }
        }

        // Start networking if applicable
        viewModelScope.launch {
            // Enable bottom bar
            applyMutation {
                copy(bottomBarEnabled = true)
            }

            // Read notification to navigate to the correct spot if applicable.
            if (stateValue().notificationData != null) {
                parseNotification(stateValue().notificationData!!)
            }
        }
    }

    /**
     * Perform a navigation according to the [notificationData].
     */
    private fun parseNotification(notificationData: NotificationData) {
        when (notificationData) {
            is NotificationData.ChatNotification -> {
                // TODO: Navigate based on notification. Dunno structure after refactor.
//                rootNavigationRepository.navigate(
//                    ResellRootRoute.CHAT(
//                        name = notificationData.name,
//                        pfp = notificationData.pfp,
//                        isBuyer = notificationData.isBuyer == "true",
//                        postJson = Json.encodeToString(post.toListing()),
//                    )
//                )
            }
        }
    }

    private fun openSharedPost(postId: String) {
        deepLinkRepository.onLinkHandled(postId)

        viewModelScope.launch {
            try {
                val listing = postRepository.getPostById(postId).toListing()
                rootNavigationRepository.navigateToPdp(listing)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error opening shared post: ", e)
                rootConfirmationRepository.showError()
            }
        }
    }

    fun onNewPostExpandClick() {
        applyMutation {
            copy(
                newPostExpanded = !newPostExpanded
            )
        }
    }

    fun onShadeTapped() {
        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewRequestClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_REQUEST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewPostClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_POST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }
}

@Singleton
class MainNavigationRepository @Inject constructor() : BaseNavigationRepository<ResellMainScreen>()
