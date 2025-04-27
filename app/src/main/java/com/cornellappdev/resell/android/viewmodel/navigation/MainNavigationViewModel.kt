package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.NotificationData
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.settings.NotificationsRepository
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    notificationsRepository: NotificationsRepository,
    mainNavigationRepository: MainNavigationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

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
