package com.cornellappdev.resell.android.viewmodel.settings

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.screens.settings.SettingsRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsLandingViewModel @Inject constructor(
    private val settingsNavigationRepository: SettingsNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val dialogRepository: RootDialogRepository,
    private val userInfoRepository: UserInfoRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val profileRepository: ProfileRepository,
    private val rootConfirmationRepository: RootConfirmationRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun onEditProfileClick() {
        settingsNavigationRepository.navigate(SettingsRoute.EditProfile)
    }

    fun onNotificationsClick() {
        settingsNavigationRepository.navigate(SettingsRoute.Notifications)
    }

    fun onPrivacyClick() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.WebViewSheet(
                url = "https://www.cornellappdev.com/privacy"
            )
        )
    }

    fun onFeedbackClick() {
        settingsNavigationRepository.navigate(SettingsRoute.Feedback)
    }

    fun onTermsClick() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.WebViewSheet(
                url = "https://www.cornellappdev.com/license/resell"
            )
        )
    }

    fun onBlockedUsersClick() {
        settingsNavigationRepository.navigate(SettingsRoute.BlockedUsers)
    }

    fun onLogoutClick() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.LogOut
        )
    }

    fun onDeleteAccountClick() {
        viewModelScope.launch {
            dialogRepository.showDialog(
                RootDialogContent.CorrectAnswerDialog(
                    title = "Delete Account",
                    description = "Once deleted, your account cannot be recovered. " +
                            "Enter your username to proceed with deletion.",
                    correctAnswer = userInfoRepository.getUserInfo().username,
                    primaryButtonText = "Delete Account",
                    onPrimaryButtonClick = {
                        dialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                        profileRepository.softDelete(
                            onSuccess = {
                                onDeleteAccountSuccess()
                            },
                            onError = {
                                dialogRepository.setPrimaryButtonState(ResellTextButtonState.ENABLED)
                                rootConfirmationRepository.showError()
                            }
                        )
                    },
                    secondaryButtonText = "Cancel",
                    onSecondaryButtonClick = {
                        dialogRepository.dismissDialog()
                    },
                    exitButton = true,
                    primaryButtonContainer = ResellTextButtonContainer.PRIMARY_RED
                )
            )
        }
    }

    private fun onDeleteAccountSuccess() {
        viewModelScope.launch {
            delay(1000)
            dialogRepository.dismissDialog()
            googleAuthRepository.signOut()
            rootNavigationRepository.navigate(ResellRootRoute.LANDING)
            delay(1000)
            dialogRepository.showDialog(
                RootDialogContent.TwoButtonDialog(
                    title = "Account Deleted",
                    description = "Thank you for being a part of the Resell experience!",
                    primaryButtonText = "Done",
                    secondaryButtonText = null,
                    onPrimaryButtonClick = {
                        dialogRepository.dismissDialog()
                    },
                    onSecondaryButtonClick = {},
                    exitButton = true
                )
            )
        }
    }
}
