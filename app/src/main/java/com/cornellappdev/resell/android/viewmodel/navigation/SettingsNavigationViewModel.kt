package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.settings.SettingsRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SettingsNavigationViewModel @Inject constructor(
    settingsNavigationRepository: SettingsNavigationRepository,
) :
    ResellViewModel<SettingsNavigationViewModel.SettingsNavigationUiState>(
        initialUiState = SettingsNavigationUiState()
    ) {

    data class SettingsNavigationUiState(
        val route: UIEvent<SettingsRoute>? = null,
        val popBackStack: UIEvent<Unit>? = null
    )

    init {
        asyncCollect(settingsNavigationRepository.routeFlow) {
            applyMutation { copy(route = it) }
        }

        asyncCollect(settingsNavigationRepository.popBackStackFlow) {
            applyMutation { copy(popBackStack = it) }
        }
    }
}

@Singleton
class SettingsNavigationRepository @Inject constructor() : BaseNavigationRepository<SettingsRoute>()
