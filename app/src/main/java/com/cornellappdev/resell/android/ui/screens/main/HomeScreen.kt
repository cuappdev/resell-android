package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.components.global.AnimatedClampedAsyncImage
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.SearchBar
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
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
            .defaultHorizontalPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
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

        MainContent(homeUiState.savedListings, homeViewModel::getImageUrlState)

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
                ResellLoadingListingScroll()
            }

            is ResellApiState.Error -> {}
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() = ResellPreview {
    val listState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    var filter by remember { mutableStateOf(HomeViewModel.HomeFilter.RECENT) }

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HomeHeader(
            activeFilter = filter,
            onFilterPressed = { filter = it },
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onSearchPressed = {}
        )
        MainContent(List(5) { dumbListing }) { mutableStateOf(ResellApiResponse.Pending) }
    }
}

val dumbListing = Listing(
    id = "1",
    title = "Dumb Listing",
    images = listOf(""),
    price = 100.0.toString(),
    categories = listOf("Electronics"),
    description = "This is a dumb listing",
    user = UserInfo(
        username = "Caleb",
        name = "Caleb",
        netId = "chs232",
        venmoHandle = "-",
        bio = "lol",
        imageUrl = "",
        id = "1",
        email = ""
    )
)

@OptIn(ExperimentalMaterial3Api::class)
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
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "resell",
                style = Style.resellBrand
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(onClick = onSearchPressed, modifier = Modifier.weight(1f))
            Icon(painter = painterResource(R.drawable.ic_filter), contentDescription = "Filter")
        }
    }
}

@Composable
private fun MainContent(
    savedListings: List<Listing>,
    getImageUrlState: (String) -> MutableState<ResellApiResponse<ImageBitmap>>
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SavedByYou(savedListings, getImageUrlState)
    }
}

@Composable
private fun SavedByYou(
    savedListings: List<Listing>,
    getImageUrlState: (String) -> MutableState<ResellApiResponse<ImageBitmap>>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Saved By You", style = Style.heading3)
            Text(text = "See All", style = Style.body2, modifier = Modifier.clickable { })//todo
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            items(savedListings) { listing ->
                val image by getImageUrlState(listing.image)
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(R.drawable.ic_appdev),
                        contentDescription = "appdev"
                    )
                } else {
                    AnimatedClampedAsyncImage(
                        image = image,
                        modifier = Modifier
                            .height(112.dp)
                            .width(112.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

    }
}