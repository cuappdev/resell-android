package com.cornellappdev.resell.android.ui.components.availability

import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AvailabilitySheetViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository
) : ResellViewModel<AvailabilitySheetViewModel.AvailabilitySheetState>(
    initialUiState = AvailabilitySheetState(
        buttonString = "",
        allAvailabilities = emptyList(),
        title = "",
        subtitle = "",
        callback = {},
        addAvailability = false,
        currentPage = 0,
        scrollRange = Pair(0, 6),
        initialAvailabilities = emptyList(),
    )
) {

    data class AvailabilitySheetState(
        private val scrollRange: Pair<Int, Int>,
        val allAvailabilities: List<LocalDateTime>,
        val buttonString: String,
        val title: String,
        val subtitle: String,
        val callback: (List<LocalDateTime>) -> Unit,
        val addAvailability: Boolean,
        val currentPage: Int,
        val initialAvailabilities: List<LocalDateTime>,
        val textButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED
    )

    fun onAvailabilityChanged(availability: List<LocalDateTime>) {
        applyMutation { copy(allAvailabilities = availability) }
    }

    fun onButtonClick() {
        applyMutation { copy(textButtonState = ResellTextButtonState.SPINNING) }
        stateValue().callback(stateValue().allAvailabilities)
    }

    init {
        asyncCollect(rootNavigationSheetRepository.rootSheetFlow) { uiEvent ->
            if (uiEvent == null || uiEvent.payload !is RootSheet.Availability) {
                return@asyncCollect
            }

            applyMutation {
                copy(
                    buttonString = uiEvent.payload.buttonString,
                    allAvailabilities = uiEvent.payload.initialTimes,
                    title = uiEvent.payload.title,
                    subtitle = uiEvent.payload.description,
                    callback = uiEvent.payload.callback,
                    addAvailability = uiEvent.payload.addAvailability,
                    initialAvailabilities = uiEvent.payload.initialTimes
                )
            }
        }
    }
}
