package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.FromHistoryHeader
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.FromPurchasesViewModel
import kotlinx.coroutines.launch

@Composable
fun FromPurchasesScreen(
    modifier: Modifier = Modifier,
    fromPurchasesViewModel: FromPurchasesViewModel = hiltViewModel(),
) {
    val fromPurchasesUiState = fromPurchasesViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize()
    ) {
        FromHistoryHeader(
            text = "From Your Purchases",
            onBack = { fromPurchasesViewModel.onBackPressed() },
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            })

        when (fromPurchasesUiState.loadedState) {
            is ResellApiState.Loading -> {
                ResellLoadingListingScroll()
            }

            ResellApiState.Error -> {}
            ResellApiState.Success -> ResellListingsScroll(
                listings = fromPurchasesUiState.listings,
                onListingPressed = fromPurchasesViewModel::onListingPressed,
            )

        }
    }
}
