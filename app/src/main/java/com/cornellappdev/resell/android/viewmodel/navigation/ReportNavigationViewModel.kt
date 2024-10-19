package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ReportNavigationViewModel @Inject constructor(
    reportNavigationRepository: ReportNavigationRepository
) :
    ResellViewModel<ReportNavigationViewModel.UiState>(
        initialUiState = UiState(
            route = null
        ),
    ) {
    data class UiState(
        val route: UIEvent<ReportScreen>?,
    )

    init {
        asyncCollect(reportNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    route = route
                )
            }
        }
    }
}

@Singleton
class ReportNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ReportScreen>()
