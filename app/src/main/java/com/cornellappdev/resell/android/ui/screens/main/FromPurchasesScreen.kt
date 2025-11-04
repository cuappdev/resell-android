package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.main.FromHistoryBody
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
    val categories = fromPurchasesUiState.listings

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

        //TODO: functionality to be added with purchase suggestions
        FromHistoryBody(modifier, categories, {}, {})

    }
}
