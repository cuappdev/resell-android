package com.cornellappdev.resell.android.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.NavHostModule
import com.cornellappdev.resell.android.model.RootNav
import com.cornellappdev.resell.android.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootNavigationViewModel @Inject constructor(
    @RootNav val navController: NavHostController,
    val rootNavigationSheetRepository: RootNavigationSheetRepository,
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
    )

    init {
        viewModelScope.launch {
            rootNavigationSheetRepository.rootSheetFlow.collect { sheetEvent ->
                applyMutation {
                    copy(sheetEvent = sheetEvent)
                }
            }
        }
    }
}
