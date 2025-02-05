package com.cornellappdev.resell.android.ui.screens.externalprofile

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.main.SearchScreen
import com.cornellappdev.resell.android.viewmodel.ExternalProfileSearchViewModel

@Composable
fun ExternalProfileSearchScreen(
    externalProfileSearchViewModel: ExternalProfileSearchViewModel = hiltViewModel()
) {
    val uiState = externalProfileSearchViewModel.collectUiStateValue()
    SearchScreen(
        query = uiState.query,
        onQueryChanged = externalProfileSearchViewModel::onQueryChanged,
        onExit = externalProfileSearchViewModel::onExit,
        onListingPressed = externalProfileSearchViewModel::onListingPressed,
        listings = uiState.listings,
        placeholder = uiState.placeholderText,
        focusKeyboard = uiState.focusKeyboard
    )
}
