package com.cornellappdev.resell.android.viewmodel.root

import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootNavigationViewModel @Inject constructor(
    rootNavigationSheetRepository: RootNavigationSheetRepository,
    rootNavigationRepository: RootNavigationRepository,
    firebaseMessagingRepository: FirebaseMessagingRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val rootDialogRepository: RootDialogRepository,
) : ResellViewModel<RootNavigationViewModel.RootNavigationUiState>(
    initialUiState = RootNavigationUiState()
) {

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
        val directlyRequestNotificationPermissions: UIEvent<Unit>? = null
    )

    init {
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

        asyncCollect(firebaseMessagingRepository.requestNotificationsEventFlow) { event ->
            applyMutation {
                copy(requestNotificationPermissions = event)
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
