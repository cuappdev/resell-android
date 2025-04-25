package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.SavedViewModel
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(
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
        Header(
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )
        when (savedUiState.loadedState) {
            is ResellApiState.Success -> {
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

@Composable
private fun Header(
    onTopPressed: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onTopPressed()
                }
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Saved",
                style = Style.heading1
            )
        }

        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
