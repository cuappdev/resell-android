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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ProfileReview
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
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
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel.ExternalProfileTab

private val ToolbarIconSize = 25.dp

@Composable
fun ExternalProfileScreen(
    profileViewModel: ExternalProfileViewModel = hiltViewModel()
) {
    val uiState = profileViewModel.collectUiStateValue()
    val staggeredState = rememberLazyStaggeredGridState()
    val headerState = rememberCollapsingHeaderState()

    // Reset tab to top to show external profile header
    LaunchedEffect(uiState.selectedTab) {
        headerState.reset()
    }

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
                onEllipsisPressed = { profileViewModel.onEllipsisPressed() },
                onSearchPressed = { profileViewModel.onSearchPressed() },
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
                    ),
                    LabeledTab(
                        count = uiState.reviewCount,
                        iconRes = R.drawable.ic_selected_star,
                        iconSize = 20.dp,
                        title = uiState.ratingLabel,
                    ),
                ),
                selectedIndex = uiState.selectedTab.ordinal,
                onTabSelected = { profileViewModel.onTabSelected(ExternalProfileTab.entries[it]) },
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
                        onListingPressed = { profileViewModel.onListingPressed(it) },
                    )

                    ExternalProfileTab.REVIEWS -> ReviewsTab(reviews = uiState.reviews)
                }
            }
        }

        if (uiState.isBlocked) {
            BlockedView(
                onEllipsisPressed = { profileViewModel.onEllipsisPressed() },
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
            modifier = Modifier.align(Alignment.Center)
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
