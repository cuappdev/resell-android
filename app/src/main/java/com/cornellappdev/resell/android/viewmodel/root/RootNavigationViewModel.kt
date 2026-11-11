package com.cornellappdev.resell.android.viewmodel.root

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.DeepLinkRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootNavigationViewModel @Inject constructor(
    rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    rootShareRepository: RootShareRepository,
    firebaseMessagingRepository: FirebaseMessagingRepository,
    private val deepLinkRepository: DeepLinkRepository,
    private val postRepository: ResellPostRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val rootDialogRepository: RootDialogRepository,
) : ResellViewModel<RootNavigationViewModel.RootNavigationUiState>(
    initialUiState = RootNavigationUiState()
) {

    companion object {
        private const val TAG = "RootNavigationViewModel"
    }

    /**
     * Root navigation UI state.
     *
     * @param sheetEvent The root sheet to display. If null, sheet should hide as a UI event.
     */
    data class RootNavigationUiState(
        val sheetEvent: UIEvent<RootSheet>? = null,
        val hideEvent: UIEvent<Unit>? = null,
        val navEvent: UIEvent<ResellRootRoute>? = null,
        val popBackStack: UIEvent<Unit>? = null,
        val requestNotificationPermissions: UIEvent<Unit>? = null,
        val directlyRequestNotificationPermissions: UIEvent<Unit>? = null,
        val shareEvent: UIEvent<String>? = null,
    )

    /**
     * Shared links stay pending until this is true.
     */
    private val onSignedInDestination = MutableStateFlow(false)

    private var openSharedPostJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                deepLinkRepository.pendingPostId,
                onSignedInDestination
            ) { postId, signedIn -> postId.takeIf { signedIn } }
                .filterNotNull()
                .collect { postId -> openSharedPost(postId) }
        }

        // Don't push a post over the landing screen if the user signs out mid-lookup.
        asyncCollect(onSignedInDestination) { signedIn ->
            if (!signedIn) openSharedPostJob?.cancel()
        }

        asyncCollect(rootNavigationSheetRepository.rootSheetFlow) { sheet ->
            applyMutation {
                copy(sheetEvent = sheet)
            }
        }

        asyncCollect(rootNavigationSheetRepository.hideFlow) { hide ->
            applyMutation {
                copy(hideEvent = hide)
            }
        }

        asyncCollect(rootNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(navEvent = route)
            }
        }

        asyncCollect(rootNavigationRepository.popBackStackFlow) { pop ->
            applyMutation {
                copy(popBackStack = pop)
            }
        }

        asyncCollect(rootShareRepository.shareEvent) { event ->
            applyMutation {
                copy(shareEvent = event)
            }
        }

        asyncCollect(firebaseMessagingRepository.requestNotificationsEventFlow) { event ->
            applyMutation {
                copy(requestNotificationPermissions = event)
            }
        }
    }

    /**
     * Reports whether the root nav graph is currently on a signed-in destination.
     */
    fun onSignedInDestinationChanged(signedIn: Boolean) {
        onSignedInDestination.value = signedIn
    }

    private fun openSharedPost(postId: String) {
        deepLinkRepository.onLinkHandled(postId)

        // Only the latest link may navigate.
        openSharedPostJob?.cancel()
        openSharedPostJob = viewModelScope.launch {
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

    fun onPermissionsAlreadyGranted() {

    }

    fun onShowRationaleUi() {
        rootDialogRepository.showDialog(
            event = RootDialogContent.TwoButtonDialog(
                title = "Notifications Required",
                description = "You must grant notifications to get notified of new messages about your orders.",
                primaryButtonText = "Grant Permissions",
                secondaryButtonText = null,
                onPrimaryButtonClick = {
                    applyMutation {
                        copy(
                            directlyRequestNotificationPermissions = UIEvent(Unit)
                        )
                    }
                    rootDialogRepository.dismissDialog()
                },
                onSecondaryButtonClick = {},
                exitButton = false,
            )
        )
    }
}
