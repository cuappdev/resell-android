package com.cornellappdev.resell.android.viewmodel.settings

import com.cornellappdev.resell.android.ui.screens.settings.SettingsRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsLandingViewModel @Inject constructor(
    private val settingsNavigationRepository: SettingsNavigationRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun onEditProfileClick() {

    }

    fun onNotificationsClick() {
        settingsNavigationRepository.navigate(SettingsRoute.Notifications)
    }

    fun onPrivacyClick() {

    }

    fun onFeedbackClick() {
        settingsNavigationRepository.navigate(SettingsRoute.Feedback)
    }

    fun onTermsClick() {

    }

    fun onBlockedUsersClick() {

    }

    fun onLogoutClick() {

    }

    fun onDeleteAccountClick() {

    }
}
