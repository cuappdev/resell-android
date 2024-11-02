package com.cornellappdev.resell.android.viewmodel.externalprofile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.externalprofile.ExternalProfileRoute
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.BaseNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ExternalNavigationViewModel @Inject constructor(
    private val externalNavigationRepository: ExternalNavigationRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<ExternalNavigationViewModel.ExternalNavUIState>(
        initialUiState = ExternalNavUIState(
            route = null,
            startingRoute = ExternalProfileRoute.ExternalProfile("")
        )
    ) {

    data class ExternalNavUIState(
        val route: UIEvent<ExternalProfileRoute>?,
        val startingRoute: ExternalProfileRoute
    )

    init {
        val args = savedStateHandle.toRoute<ResellRootRoute.EXTERNAL_PROFILE>()

        applyMutation {
            copy(
                route = UIEvent(ExternalProfileRoute.ExternalProfile(args.id)),
                startingRoute = ExternalProfileRoute.ExternalProfile(args.id)
            )
        }

        asyncCollect(externalNavigationRepository.routeFlow) { event ->
            applyMutation {
                copy(
                    route = event
                )
            }
        }
    }
}

@Singleton
class ExternalNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ExternalProfileRoute>()
