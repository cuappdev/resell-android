package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ReportNavigationViewModel @Inject constructor(
    reportNavigationRepository: ReportNavigationRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<ReportNavigationViewModel.UiState>(
        initialUiState = UiState(
            route = null
        ),
    ) {
    data class UiState(
        val route: UIEvent<ReportScreen>?,
        val initialPage: ReportScreen? = null,
    )

    init {
        asyncCollect(reportNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    route = route
                )
            }
        }

        val navArgs = savedStateHandle.toRoute<ResellRootRoute.REPORT>()
        applyMutation {
            copy(
                initialPage = ReportScreen.Reason(
                    reportType = navArgs.reportType,
                    postId = navArgs.postId,
                    userId = navArgs.userId
                )
            )
        }
    }
}

@Singleton
class ReportNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ReportScreen>()
