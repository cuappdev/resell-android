package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.FromHistoryBody
import com.cornellappdev.resell.android.ui.components.main.FromHistoryHeader
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.FromSearchesViewModel
import kotlinx.coroutines.launch

@Composable
fun FromSearchScreen(
    modifier: Modifier = Modifier,
    fromSearchViewModel: FromSearchesViewModel = hiltViewModel(),
) {
    val fromSearchUiState = fromSearchViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()
    val categories = fromSearchUiState.listings

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize()
    ) {
        FromHistoryHeader(
            text = "From Your Searches",
            onBack = { fromSearchViewModel.onBackPressed() },
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            })

        Spacer(modifier = Modifier.height(Padding.medium))

        when (fromSearchUiState.loadedState) {
            is ResellApiState.Loading -> {
                ResellLoadingListingScroll()
            }

            ResellApiState.Error -> {}
            ResellApiState.Success -> FromHistoryBody(
                modifier,
                categories,
                { fromSearchViewModel.onListingPressed(it) },
                { fromSearchViewModel.hideSearch(it) }
            )

        }


    }
}
