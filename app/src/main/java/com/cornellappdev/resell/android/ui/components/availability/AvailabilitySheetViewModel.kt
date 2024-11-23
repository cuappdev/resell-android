package com.cornellappdev.resell.android.ui.components.availability

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
        description = "",
        callback = {},
        addAvailability = false,
        currentPage = 0,
        scrollRange = Pair(0, 6),
        initialAvailabilities = emptyList(),
    )
) {

    data class AvailabilitySheetState(
        private val allAvailabilities: List<LocalDateTime>,
        private val scrollRange: Pair<Int, Int>,
        val buttonString: String,
        val title: String,
        val description: String,
        val callback: (List<LocalDateTime>) -> Unit,
        val addAvailability: Boolean,
        val currentPage: Int,
        val initialAvailabilities: List<LocalDateTime>,
    )

    fun onAvailabilityChanged(availability: List<LocalDateTime>) {
        applyMutation { copy(allAvailabilities = availability) }
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
                    description = uiEvent.payload.description,
                    callback = uiEvent.payload.callback,
                    addAvailability = uiEvent.payload.addAvailability,
                    initialAvailabilities = uiEvent.payload.initialTimes
                )
            }
        }
    }
}
