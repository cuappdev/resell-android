package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton

@Composable
fun AvailabilitySheet(
    availabilitySheetViewModel: AvailabilitySheetViewModel = hiltViewModel()
) {
    val uiState = availabilitySheetViewModel.collectUiStateValue()
    Column(
        modifier = Modifier.fillMaxHeight(.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectableAvailabilityPager(
            initialSelectedAvailabilities = uiState.initialAvailabilities,
            setSelectedAvailabilities = availabilitySheetViewModel::onAvailabilityChanged,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .weight(1f),
            title = uiState.title,
            subtitle = uiState.subtitle,
            gridSelectionType = uiState.gridSelectionType,
            setProposalTime = availabilitySheetViewModel::setProposalTime
        )

        ResellTextButton(
            text = uiState.buttonString,
            state = uiState.textButtonState,
            onClick = {
                availabilitySheetViewModel.onButtonClick()
            },
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}
