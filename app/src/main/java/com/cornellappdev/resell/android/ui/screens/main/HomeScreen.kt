package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.components.global.AnimatedClampedAsyncImage
import com.cornellappdev.resell.android.ui.components.global.resellListingScroll
import com.cornellappdev.resell.android.ui.components.global.resellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.SearchBar
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onSavedPressed: () -> Unit
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
            onFilter = {},// TODO
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onSearchPressed = homeViewModel::onSearchPressed
        )

        MainContent(
            savedListings = homeUiState.savedListings,
            getImageUrlState = homeViewModel::getImageUrlState,
            onSavedPressed = onSavedPressed,
            toPost = homeViewModel::onListingPressed,
            loadedState = homeUiState.loadedState,
            filteredListings = homeUiState.filteredListings,
            onListingPressed = homeViewModel::onListingPressed
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() = ResellPreview {
    val listState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HomeHeader(
            onFilter = {},
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onSearchPressed = {}
        )

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
        MainContent(
            List(5) { dumbListing },
            getImageUrlState = { mutableStateOf(ResellApiResponse.Pending) },
            onSavedPressed = {},
            toPost = {},
            loadedState = ResellApiState.Success,
            filteredListings = List(10) { dumbListing },
            onListingPressed = {}
        )
    }
}


@Composable
private fun HomeHeader(
    onFilter: () -> Unit = {},
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
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Filter",
                modifier = Modifier.clickable(onClick = onFilter)
            )
        }
    }
}

@Composable
private fun MainContent(
    savedListings: List<Listing>,
    getImageUrlState: (String) -> MutableState<ResellApiResponse<ImageBitmap>>,
    onSavedPressed: () -> Unit,
    toPost: (Listing) -> Unit,
    loadedState: ResellApiState,
    filteredListings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
) {
    val preview = LocalInspectionMode.current
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 12.dp
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            SavedByYou(
                savedListings,
                getImageUrlState,
                onSavedPressed,
                toPost
            )
            Spacer(Modifier.height(12.dp))
        }
        item(span = StaggeredGridItemSpan.FullLine) {
            ShopByCategory()
            Spacer(Modifier.height(12.dp))
        }
        recentListings(
            loadedState = loadedState,
            filteredListings = filteredListings,
            onListingPressed = onListingPressed,
            preview = preview
        )
    }
}

@Composable
private fun SavedByYou(
    savedListings: List<Listing>,
    getImageUrlState: (String) -> MutableState<ResellApiResponse<ImageBitmap>>,
    onSavedPressed: () -> Unit,
    toPost: (Listing) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Saved By You", style = Style.heading3)
            Text(text = "See All", style = Style.body2, modifier = Modifier.clickable {
                onSavedPressed()
            })
        }
        if (savedListings.isEmpty()) {
            NoSaved()
        } else {
            SavedListingsRow(savedListings, getImageUrlState, toPost)
        }
    }
}

@Composable
private fun SavedListingsRow(
    savedListings: List<Listing>,
    getImageUrlState: (String) -> MutableState<ResellApiResponse<ImageBitmap>>,
    toPost: (Listing) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        items(savedListings) { listing ->
            val image by getImageUrlState(listing.image)
            if (LocalInspectionMode.current) {
                Image(
                    painter = painterResource(R.drawable.ic_appdev),
                    contentDescription = null,
                    modifier = Modifier
                        .height(112.dp)
                        .width(112.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            } else {
                AnimatedClampedAsyncImage(
                    image = image,
                    modifier = Modifier
                        .height(112.dp)
                        .width(112.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            toPost(listing)
                        }
                )
            }
        }
    }
}

@Composable
private fun NoSaved() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "You haven't saved any listings yet.", style = Style.body1)

        val inlineContent = mapOf(
            Pair(
                "1",
                InlineTextContent(
                    Placeholder(
                        width = 12.sp,
                        height = 12.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark),
                        contentDescription = "Bookmark Button"
                    )
                }
            )
        )

        val text = buildAnnotatedString {
            append("Tap ")
            appendInlineContent(id = "1", "[icon]")
            append(" on a listing to save")
        }
        Text(text = text, style = Style.body1, inlineContent = inlineContent)
    }
}

@Composable
private fun ShopByCategory() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Shop by Category", style = Style.heading3)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val images = listOf(
                R.drawable.shoes,
                R.drawable.books,
                R.drawable.pencilcase,
                R.drawable.airpods_max,
                R.drawable.color_palette,
                R.drawable.basketball,
                R.drawable.other
            )
            val labels = listOf(
                "Clothing",
                "Books",
                "School",
                "Electronics",
                "Handmade",
                "Sports & Outdoors",
                "Other"
            )
            val backgroundColors = listOf(
                Color(0x80CA95A3),
                Color(0x80316054),
                Color(0x80A4B7AB),
                Color(0x80D795AB),
                Color(0x80E3B570),
                Color(0x8073A2AB),
                Color(0x80E2B56E)
            )
            items(min(min(images.size, labels.size), backgroundColors.size)) { idx ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(color = backgroundColors[idx])
                            .align(alignment = Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(images[idx]),
                            contentDescription = labels[idx],
                            modifier = Modifier
                                .width(57.dp)
                                .height(57.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(text = labels[idx], style = Style.title4)
                }
            }
        }
    }
}

private fun LazyStaggeredGridScope.recentListings(
    loadedState: ResellApiState,
    filteredListings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    preview: Boolean = false
) {
    item(span = StaggeredGridItemSpan.FullLine) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Recent Listings", style = Style.heading3)
        }
    }
    val randomList = List(50) { i ->
        when (i) {
            0 -> true
            1 -> false
            else -> Random.nextBoolean()
        }
    }
    when (loadedState) {
        is ResellApiState.Success -> {
            if (preview) {
                resellLoadingListingScroll(numCards = 5, randomList = randomList)
            } else {
                resellListingScroll(
                    listings = filteredListings,
                    onListingPressed = onListingPressed
                )
            }
        }

        is ResellApiState.Loading -> {
            resellLoadingListingScroll(numCards = Int.MAX_VALUE - 1, randomList = randomList)
        }

        is ResellApiState.Error -> {}
    }

}

