package com.cornellappdev.resell.android.ui.screens.externalprofile

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ProfileReview
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.components.global.LabeledTab
import com.cornellappdev.resell.android.ui.components.global.ResellLabeledTabBar
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.profile.ExternalProfileHeader
import com.cornellappdev.resell.android.ui.components.profile.ProfileEmptyState
import com.cornellappdev.resell.android.ui.components.profile.ResellLoadingReviewsScroll
import com.cornellappdev.resell.android.ui.components.profile.ResellReviewsScroll
import com.cornellappdev.resell.android.ui.components.profile.collapsingHeader
import com.cornellappdev.resell.android.ui.components.profile.rememberCollapsingHeaderState
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.LocalInfiniteShimmer
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel.ExternalProfileState
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel.ExternalProfileTab

private val ToolbarIconSize = 25.dp
private val ToolbarTitleGap = 8.dp

@Composable
fun ExternalProfileScreen(
    profileViewModel: ExternalProfileViewModel = hiltViewModel()
) {
    ExternalProfileContent(
        uiState = profileViewModel.collectUiStateValue(),
        onTabSelected = { profileViewModel.onTabSelected(it) },
        onListingPressed = { profileViewModel.onListingPressed(it) },
        onEllipsisPressed = { profileViewModel.onEllipsisPressed() },
        onSearchPressed = { profileViewModel.onSearchPressed() },
    )
}

@Composable
private fun ExternalProfileContent(
    uiState: ExternalProfileState,
    onTabSelected: (ExternalProfileTab) -> Unit,
    onListingPressed: (Listing) -> Unit,
    onEllipsisPressed: () -> Unit,
    onSearchPressed: () -> Unit,
) {
    val staggeredState = rememberLazyStaggeredGridState()
    val headerState = rememberCollapsingHeaderState()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .then(
                    if (uiState.isBlocked) Modifier.blur(5.dp)
                    else Modifier
                )
                .nestedScroll(headerState.connection)
        ) {
            ExternalProfileToolbar(
                username = uiState.shopName,
                onEllipsisPressed = onEllipsisPressed,
                onSearchPressed = onSearchPressed,
            )

            ExternalProfileHeader(
                imageUrl = uiState.imageUrl,
                name = uiState.firstName,
                averageRating = uiState.averageRating,
                reviewCount = uiState.reviewCount,
                showRating = uiState.reviewsLoaded,
                bio = uiState.displayBio,
                modifier = Modifier.collapsingHeader(headerState)
            )

            ResellLabeledTabBar(
                tabs = listOf(
                    LabeledTab(
                        count = uiState.listingCount,
                        iconRes = R.drawable.ic_shop,
                        contentDescription = "Listings, ${uiState.listingCount}",
                    ),
                    LabeledTab(
                        count = uiState.reviewCount,
                        iconRes = R.drawable.ic_selected_star,
                        contentDescription = if (uiState.ratingLabel.isNotEmpty()) {
                            "Reviews, ${uiState.ratingLabel} average rating, ${uiState.reviewCount} reviews"
                        } else {
                            "Reviews, ${uiState.reviewCount}"
                        },
                        iconSize = 20.dp,
                        title = uiState.ratingLabel,
                    ),
                ),
                selectedIndex = uiState.selectedTab.ordinal,
                onTabSelected = { index ->
                    val tab = ExternalProfileTab.entries[index]
                    // Show the header again when switching tabs.
                    if (tab != uiState.selectedTab) headerState.reset()
                    onTabSelected(tab)
                },
            )

            AnimatedContent(
                targetState = uiState.selectedTab,
                label = "external profile content",
                transitionSpec = simpleFadeInOut,
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    ExternalProfileTab.LISTINGS -> ListingsTab(
                        listings = uiState.shopListings,
                        listState = staggeredState,
                        onListingPressed = onListingPressed,
                    )

                    ExternalProfileTab.REVIEWS -> ReviewsTab(reviews = uiState.reviews)
                }
            }
        }

        if (uiState.isBlocked) {
            BlockedView(
                onEllipsisPressed = onEllipsisPressed,
            )
        }
    }
}

@Composable
private fun ListingsTab(
    listings: ResellApiResponse<List<Listing>>,
    listState: LazyStaggeredGridState,
    onListingPressed: (Listing) -> Unit,
) {
    when (listings) {
        is ResellApiResponse.Pending -> ResellLoadingListingScroll()

        is ResellApiResponse.Error -> ProfileMessage(
            title = "Something went wrong",
            body = "We couldn't load this user's listings. Please try again later."
        )

        is ResellApiResponse.Success -> ResellListingsScroll(
            listings = listings.data,
            onListingPressed = onListingPressed,
            listState = listState,
            paddedTop = 16.dp,
            emptyState = {
                ProfileMessage(
                    title = "No listings posted",
                    body = "This user hasn't posted any listings yet"
                )
            }
        )
    }
}

@Composable
private fun ReviewsTab(reviews: ResellApiResponse<List<ProfileReview>>) {
    when (reviews) {
        is ResellApiResponse.Pending -> ResellLoadingReviewsScroll()

        is ResellApiResponse.Error -> ProfileMessage(
            title = "Something went wrong",
            body = "We couldn't load this user's reviews. Please try again later."
        )

        is ResellApiResponse.Success -> ResellReviewsScroll(reviews = reviews.data)
    }
}

@Composable
private fun ProfileMessage(title: String, body: String) {
    ProfileEmptyState(
        title = title,
        body = body,
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 60.dp)
    )
}

@Composable
private fun ExternalProfileToolbar(
    username: String,
    onEllipsisPressed: () -> Unit,
    onSearchPressed: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .statusBarsPadding()
            .height(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_ellipse),
            contentDescription = "options",
            modifier = Modifier
                .defaultHorizontalPadding()
                .size(ToolbarIconSize)
                .align(Alignment.CenterStart)
                .clickableNoIndication { onEllipsisPressed() }
        )

        Text(
            text = "@$username",
            style = Style.heading3,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Padding.leftRight + ToolbarIconSize + ToolbarTitleGap)
                .align(Alignment.Center)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "search",
            modifier = Modifier
                .defaultHorizontalPadding()
                .size(ToolbarIconSize)
                .align(Alignment.CenterEnd)
                .clickableNoIndication { onSearchPressed() }
        )
    }
}

@Preview
@Composable
private fun BlockedView(
    onEllipsisPressed: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f))
            .clickableNoIndication {  }
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ellipse),
                    contentDescription = "settings",
                    modifier = Modifier
                        .defaultHorizontalPadding()
                        .padding(top = 8.dp)
                        .size(ToolbarIconSize)
                        .align(Alignment.TopStart)
                        .clickableNoIndication { onEllipsisPressed() },
                    tint = Color.White
                )
            }
        }

        Text(
            text = "This profile is blocked",
            style = title1,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExternalProfileContentPreview() = ResellPreview {
    val seller = UserInfo(
        username = "ravina",
        name = "Ravina Patel",
        netId = "rp123",
        venmoHandle = "",
        bio = "",
        imageUrl = "",
        id = "seller",
        email = "rp123@cornell.edu",
    )
    val listings = List(4) {
        Listing(
            id = "$it",
            title = "Item $it",
            images = listOf(""),
            price = "${(it + 1) * 10}",
            categories = listOf("Furniture"),
            description = "",
            user = seller,
        )
    }
    val reviews = listOf(
        ProfileReview(
            id = "1",
            rating = 5,
            comment = "Super easy to meet up with and the item was exactly as described!",
            dateLabel = "Oct 12, 2026",
            reviewerName = "Ryan",
        ),
        ProfileReview(
            id = "2",
            rating = 4,
            comment = "",
            dateLabel = "Oct 3, 2026",
            reviewerName = "Anonymous",
        ),
    )

    CompositionLocalProvider(value = LocalInfiniteShimmer provides Color.LightGray) {
        ExternalProfileContent(
            uiState = ExternalProfileState(
                shopName = seller.username,
                vendorName = seller.name,
                bio = "",
                imageUrl = "",
                loadedState = ResellApiState.Success,
                shopListings = ResellApiResponse.Success(listings),
                archiveListings = ResellApiResponse.Pending,
                reviews = ResellApiResponse.Success(reviews),
                uid = seller.id,
                blockedUsers = listOf(),
            ),
            onTabSelected = {},
            onListingPressed = {},
            onEllipsisPressed = {},
            onSearchPressed = {},
        )
    }
}
