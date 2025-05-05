package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.FilterCategory
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.components.global.AnimatedClampedAsyncImage
import com.cornellappdev.resell.android.ui.components.global.resellListingScroll
import com.cornellappdev.resell.android.ui.components.global.resellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.FilterBottomSheet
import com.cornellappdev.resell.android.ui.components.main.ResellSearchBar
import com.cornellappdev.resell.android.ui.components.nav.NAVBAR_HEIGHT
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onSavedPressed: () -> Unit,
    setNavBarShown: (Boolean) -> Unit,
    onCategoryPressed: (FilterCategory) -> Unit
) {
    val homeUiState = homeViewModel.collectUiStateValue()
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(sheetState.isVisible) {
        setNavBarShown(!sheetState.isVisible)
    }
    HomeScreenHelper(
        filter = homeUiState.activeFilter,
        onFilterPressed = {
            coroutineScope.launch {
                sheetState.expand()
            }
        },
        onFilterChanged = {
            homeViewModel.onFilterChanged(it)
            coroutineScope.launch {
                sheetState.hide()
            }
        },
        sheetState = sheetState,
        onCategoryPressed = onCategoryPressed,
        onSearchPressed = homeViewModel::onSearchPressed,
        savedListings = homeUiState.savedListings,
        onSavedPressed = onSavedPressed,
        loadedState = homeUiState.loadedState,
        filteredListings = homeUiState.listings,
        onListingPressed = homeViewModel::onListingPressed,
        savedImagesResponses = homeUiState.savedImageResponses,
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenHelper(
    filter: ResellFilter,
    onFilterPressed: () -> Unit,
    onFilterChanged: (ResellFilter) -> Unit,
    sheetState: SheetState,
    onCategoryPressed: (FilterCategory) -> Unit,
    onSearchPressed: () -> Unit,
    savedListings: List<Listing>,
    onSavedPressed: () -> Unit,
    loadedState: ResellApiState,
    filteredListings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    savedImagesResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = NAVBAR_HEIGHT.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeHeader(
            onFilter = onFilterPressed,
            onSearchPressed = onSearchPressed,
            modifier = Modifier.defaultHorizontalPadding()
        )

        MainContent(
            savedListings = savedListings,
            onSavedPressed = onSavedPressed,
            toPost = onListingPressed,
            onCategoryPressed = onCategoryPressed,
            loadedState = loadedState,
            filteredListings = filteredListings,
            onListingPressed = onListingPressed,
            savedImagesResponses = savedImagesResponses
        )
    }
    if (sheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            FilterBottomSheet(filter = filter, onFilterChanged = onFilterChanged)
        }
    }
}

@Composable
private fun HomeHeader(
    onFilter: () -> Unit,
    onSearchPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "resell",
                style = Style.resellBrand
            )
            Icon(
                painter = painterResource(R.drawable.bell),
                contentDescription = "Notifications",
                modifier = Modifier.clickable(onClick = {})//todo
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResellSearchBar(onClick = onSearchPressed, modifier = Modifier.weight(1f))
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
    onSavedPressed: () -> Unit,
    toPost: (Listing) -> Unit,
    loadedState: ResellApiState,
    onCategoryPressed: (FilterCategory) -> Unit,
    filteredListings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    savedImagesResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>
) {
    val preview = LocalInspectionMode.current
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxWidth(),
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        savedByYou(
            savedListings,
            onSavedPressed,
            toPost,
            savedImagesResponses
        )
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(24.dp))
        }
        shopByCategory(onCategoryPressed = onCategoryPressed)
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(24.dp))
        }
        recentListings(
            loadedState = loadedState,
            filteredListings = filteredListings,
            onListingPressed = onListingPressed,
            preview = preview
        )
    }
}

private fun LazyStaggeredGridScope.savedByYou(
    savedListings: List<Listing>,
    onSavedPressed: () -> Unit,
    toPost: (Listing) -> Unit,
    savedImagesResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>
) {
    item(span = StaggeredGridItemSpan.FullLine) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Saved By You", style = Style.heading3)
            Text(text = "See All", style = Style.body2, modifier = Modifier.clickable {
                onSavedPressed()
            })
        }
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        Spacer(modifier = Modifier.height(12.dp))
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        if (savedListings.isEmpty()) {
            SavedEmptyState()
        } else {
            ForceHorizontalOffset(offset = Padding.leftRight) { modifier ->
                SavedListingsRow(
                    savedListings,
                    toPost,
                    savedImagesResponses,
                    modifier = modifier
                )
            }
        }

    }
}

/**
 * Allows [content] to ignore parent padding and offset itself towards the start and end by
 * [offset].
 *
 * This modifier can be applied where there is a need for a scrollable list to disobey the content
 * padding of its parent. This is optimal for LazyStaggeredGrids, as manually calculating padding
 * for individual staggered grid items in multiple columns is impractical.
 *
 * @see Modifier.layout
 */
@Composable
private fun ForceHorizontalOffset(
    offset: Dp,
    content: @Composable (Modifier) -> Unit
) {
    val offsetPx = with(LocalDensity.current) { offset.roundToPx() }
    content(
        Modifier
            .layout { measurable, constraints ->
                // Expand given constraints by increasing width by twice the given offset
                val looseConstraints = constraints.offset(horizontal = offsetPx * 2, vertical = 0)

                // Measure the layout based on new constraints
                val placeable = measurable.measure(looseConstraints)
                layout(placeable.width, placeable.height) {
                    // Where the composable is placed in the layout
                    placeable.placeRelative(0, 0)
                }
            }
            .fillMaxWidth()
    )
}

@Composable
private fun SavedListingsRow(
    savedListings: List<Listing>,
    toPost: (Listing) -> Unit,
    savedImageResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        itemsIndexed(savedListings) { index, listing ->
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
                val image: MutableState<ResellApiResponse<ImageBitmap>> = remember {
                    savedImageResponses.getOrNull(index) ?: mutableStateOf(ResellApiResponse.Error)
                }
                ResellSavedCard(
                    onClick = { toPost(listing) },
                    imageResponse = image
                )
            }
        }
    }
}

@Composable
private fun SavedEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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

private enum class CategoryItem(
    val image: Int,
    val label: String,
    val backgroundColor: Color,
    val category: FilterCategory
) {
    CLOTHING(
        image = R.drawable.shoes,
        label = "Clothing",
        backgroundColor = Color(0x80CA95A3),
        category = FilterCategory.CLOTHING
    ),
    BOOKS(
        image = R.drawable.books,
        label = "Books",
        backgroundColor = Color(0x80316054),
        category = FilterCategory.BOOKS
    ),
    SCHOOL(
        image = R.drawable.pencilcase,
        label = "School",
        backgroundColor = Color(0x80A4B7AB),
        category = FilterCategory.SCHOOL
    ),
    ELECTRONICS(
        image = R.drawable.airpods_max,
        label = "Electronics",
        backgroundColor = Color(0x80D795AB),
        category = FilterCategory.ELECTRONICS
    ),
    HANDMADE(
        image = R.drawable.color_palette,
        label = "Handmade",
        backgroundColor = Color(0x80E3B570),
        category = FilterCategory.HANDMADE
    ),
    SPORTS(
        image = R.drawable.football,
        label = "Sports & Outdoors",
        backgroundColor = Color(0x8073A2AB),
        category = FilterCategory.SPORTS
    ),
    OTHER(
        image = R.drawable.gift,
        label = "Other",
        backgroundColor = Color(0x80E2B56E),
        category = FilterCategory.OTHER
    )
}

private fun LazyStaggeredGridScope.shopByCategory(onCategoryPressed: (FilterCategory) -> Unit) {
    item(span = StaggeredGridItemSpan.FullLine) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Shop by Category", style = Style.heading3)
        }
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        Spacer(modifier = Modifier.height(12.dp))
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        ForceHorizontalOffset(offset = Padding.leftRight) { modifier ->
            CategoryRow(modifier, onCategoryPressed)
        }
    }
}

@Composable
private fun CategoryRow(modifier: Modifier, onCategoryPressed: (FilterCategory) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier
    ) {
        items(CategoryItem.entries.toTypedArray()) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.width(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color = category.backgroundColor)
                        .align(alignment = Alignment.CenterHorizontally)
                        .clickable {
                            onCategoryPressed(category.category)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(category.image),
                        contentDescription = category.label,
                        modifier = Modifier
                            .width(57.dp)
                            .height(57.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = category.label, style = Style.title4,
                    textAlign = TextAlign.Center
                )
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Recent Listings", style = Style.heading3)
        }
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        Spacer(
            modifier = Modifier
                .height(12.dp)
        )
    }
    when (loadedState) {
        is ResellApiState.Success -> {
            if (preview) {
                resellLoadingListingScroll(
                    numCards = Int.MAX_VALUE - 200,
                    addVerticalPadding = true
                )
            } else {
                resellListingScroll(
                    listings = filteredListings,
                    onListingPressed = onListingPressed,
                    addVerticalPadding = true
                )
            }
        }

        is ResellApiState.Loading -> {
            resellLoadingListingScroll(numCards = Int.MAX_VALUE - 200, addVerticalPadding = true)
        }

        is ResellApiState.Error -> {}
    }
}

@Composable
private fun ResellSavedCard(
    onClick: () -> Unit,
    imageResponse: MutableState<ResellApiResponse<ImageBitmap>>,
) {
    AnimatedClampedAsyncImage(
        image = imageResponse.value,
        modifier = Modifier
            .height(112.dp)
            .width(112.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
    )
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
@Preview
@Composable
private fun HomeScreenPreview() = ResellPreview {
    HomeScreenHelper(
        filter = ResellFilter(),
        onFilterPressed = {},
        onFilterChanged = {},
        sheetState = rememberModalBottomSheetState(),
        onSearchPressed = {},
        savedListings = List(5) { dumbListing },
        onSavedPressed = {},
        loadedState = ResellApiState.Loading,
        filteredListings = List(10) { dumbListing },
        onListingPressed = {},
        savedImagesResponses = List(5) {
            mutableStateOf(ResellApiResponse.Pending)
        },
        onDismissRequest = {},
        onCategoryPressed = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SavedEmptyStatePreview() = ResellPreview {
    HomeScreenHelper(
        filter = ResellFilter(),
        onFilterPressed = {},
        onFilterChanged = {},
        sheetState = rememberModalBottomSheetState(),
        onSearchPressed = {},
        savedListings = emptyList(),
        onSavedPressed = { },
        loadedState = ResellApiState.Loading,
        filteredListings = List(10) { dumbListing },
        onListingPressed = {},
        savedImagesResponses = emptyList(),
        onDismissRequest = {},
        onCategoryPressed = {}
    )
}