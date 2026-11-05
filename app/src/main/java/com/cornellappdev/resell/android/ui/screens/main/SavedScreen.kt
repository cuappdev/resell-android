package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.EmptySavedComponent
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.components.main.FromHistoryHeader
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.SavedViewModel
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(
    onExit: () -> Unit = {},
    savedViewModel: SavedViewModel = hiltViewModel(),
) {
    val savedUiState = savedViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize()
    ) {
        FromHistoryHeader(
            text = "Saved",
            onBack = onExit,
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )

        Spacer(modifier = Modifier.height(Padding.medium))

        when (savedUiState.loadedState) {
            is ResellApiState.Success -> {
                if (savedUiState.listings.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptySavedComponent(
                            modifier = Modifier
                        )
                    }
                }
                ResellListingsScroll(
                    listings = savedUiState.listings,
                    onListingPressed = savedViewModel::onListingPressed,
                    listState = listState
                )
            }

            is ResellApiState.Loading -> {
                ResellLoadingListingScroll()
            }

            is ResellApiState.Error -> {}
        }
    }
}
