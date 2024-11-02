package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.main.SearchScreen
import com.cornellappdev.resell.android.viewmodel.AllSearchViewModel

@Composable
fun AllSearchScreen(
    allSearchViewModel: AllSearchViewModel = hiltViewModel()
) {
    val uiState = allSearchViewModel.collectUiStateValue()
    SearchScreen(
        query = uiState.query,
        onQueryChanged = allSearchViewModel::onQueryChanged,
        onExit = allSearchViewModel::onExit,
        onListingPressed = allSearchViewModel::onListingPressed,
        listings = uiState.listings,
        placeholder = "Search listings..."
    )
}
