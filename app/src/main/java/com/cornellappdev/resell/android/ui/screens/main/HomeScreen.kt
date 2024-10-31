package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
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
            onNotificationPressed = homeViewModel::onNotificationPressed,
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )

        when (homeUiState.loadedState) {
            is ResellApiState.Success -> {
                ResellListingsScroll(
                    listings = homeUiState.listings,
                    onListingPressed = {
                        homeViewModel.onListingPressed(it)
                    },
                    listState = listState,
                )
            }

            is ResellApiState.Loading -> {}

            is ResellApiState.Error -> {}
        }
    }
}

@Composable
private fun HomeHeader(
    activeFilter: HomeViewModel.HomeFilter,
    onFilterPressed: (HomeViewModel.HomeFilter) -> Unit = {},
    onNotificationPressed: () -> Unit = {},
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
                .defaultHorizontalPadding()
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "resell",
                style = Style.resellBrand
            )
            Row{
                Box (
                    modifier = Modifier
                        .clickableNoIndication { onNotificationPressed() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification_bell),
                        contentDescription = "notifications",
                        tint = Primary,
                        modifier = Modifier
                            .height(28.dp)
                            .width(30.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "search",
                    tint = Primary,
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }

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
