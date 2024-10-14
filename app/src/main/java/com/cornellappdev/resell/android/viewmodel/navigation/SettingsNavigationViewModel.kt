package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.settings.SettingsRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsNavigationViewModel @Inject constructor(
    settingsNavigationRepository: SettingsNavigationRepository,
) :
    ResellViewModel<SettingsNavigationViewModel.SettingsNavigationUiState>(
        initialUiState = SettingsNavigationUiState()
    ) {

    data class SettingsNavigationUiState(
        val route: UIEvent<SettingsRoute>? = null
    )

    init {
        asyncCollect(settingsNavigationRepository.routeFlow) {
            applyMutation { copy(route = it) }
        }
    }
}
