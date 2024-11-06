package com.cornellappdev.resell.android.viewmodel.root

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
        val popBackStack: UIEvent<Unit>? = null
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
    }
}
