package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AvailabilitySheet(
    availabilitySheetViewModel: AvailabilitySheetViewModel = hiltViewModel()
) {
    val uiState = availabilitySheetViewModel.collectUiStateValue()
    SelectableAvailabilityPager(
        initialSelectedAvailabilities = uiState.initialAvailabilities,
        setSelectedAvailabilities = availabilitySheetViewModel::onAvailabilityChanged
    )
}
