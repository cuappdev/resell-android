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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellTag
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState = homeViewModel.collectUiStateValue()
    val listState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HomeHeader(
            activeFilter = homeUiState.activeFilter,
            onFilterPressed = homeViewModel::onToggleFilter,
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onSearchPressed = homeViewModel::onSearchPressed
        )

        when (homeUiState.loadedState) {
            is ResellApiState.Success -> {
                ResellListingsScroll(
                    listings = homeUiState.filteredListings,
                    onListingPressed = {
                        homeViewModel.onListingPressed(it)
                    },
                    listState = listState,
                )
            }

            is ResellApiState.Loading -> {
                ResellLoadingListingsScroll()
            }

            is ResellApiState.Error -> {}
        }
    }
}

@Composable
private fun HomeHeader(
    activeFilter: HomeViewModel.HomeFilter,
    onFilterPressed: (HomeViewModel.HomeFilter) -> Unit = {},
    onTopPressed: () -> Unit,
    onSearchPressed: () -> Unit,
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
                .defaultHorizontalPadding()
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "resell",
                style = Style.resellBrand
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search",
                tint = Primary,
                modifier = Modifier
                    .size(25.dp)
                    .clickableNoIndication {
                        onSearchPressed()
                    }
            )
        }

        // filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Padding.medium, Alignment.Start),
        ) {
            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }

            items(items = HomeViewModel.HomeFilter.entries) { filter ->
                ResellTag(
                    text = filter.name.lowercase().replaceFirstChar {
                        it.uppercase()
                    },
                    active = filter == activeFilter,
                    onClick = { onFilterPressed(filter) }
                )
            }

            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }
        }

        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
