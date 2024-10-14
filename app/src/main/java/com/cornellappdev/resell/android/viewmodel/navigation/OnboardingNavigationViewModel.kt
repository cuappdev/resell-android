package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.onboarding.ResellOnboardingScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingNavigationViewModel @Inject constructor(
    val onboardingNavigationRepository: OnboardingNavigationRepository,
) : ResellViewModel<OnboardingNavigationViewModel.OnboardingNavigationUiState>(
    initialUiState = OnboardingNavigationUiState()
) {

    data class OnboardingNavigationUiState(
        val route: UIEvent<ResellOnboardingScreen>? = null
    )
    init {
        asyncCollect(onboardingNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    route = route
                )
            }
        }
    }
}
