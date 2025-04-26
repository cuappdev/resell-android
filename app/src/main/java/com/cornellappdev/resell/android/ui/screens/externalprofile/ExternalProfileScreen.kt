package com.cornellappdev.resell.android.ui.screens.externalprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.profile.ProfileEmptyState
import com.cornellappdev.resell.android.ui.components.profile.ProfileHeader
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel

@Composable
fun ExternalProfileScreen(
    profileViewModel: ExternalProfileViewModel = hiltViewModel()
) {
    val uiState = profileViewModel.collectUiStateValue()
    val staggeredState = rememberLazyStaggeredGridState()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .then(
                    if (uiState.isBlocked) Modifier.blur(5.dp)
                    else Modifier
                )
        ) {
            ProfileHeader(
                imageUrl = uiState.imageUrl,
                shopName = uiState.shopName,
                vendorName = uiState.vendorName,
                bio = uiState.bio,
                selectedTab = null,
                leftIcon = R.drawable.ic_ellipse,
                rightIcon = R.drawable.ic_search,
                onLeftPressed = { profileViewModel.onEllipsisPressed() },
                onRightPressed = { profileViewModel.onSearchPressed() },
                onTabSelected = {}
            )

            when (uiState.shopListings) {
                is ResellApiResponse.Pending -> {
                    ResellLoadingListingScroll()
                }

                is ResellApiResponse.Error -> {

                }

                is ResellApiResponse.Success -> {
                    ResellListingsScroll(
                        listings = uiState.shopListings.data,
                        onListingPressed = { profileViewModel.onListingPressed(it) },
                        listState = staggeredState,
                        paddedTop = 12.dp,
                        emptyState = {
                            ProfileEmptyState(
                                title = "No listings posted",
                                body = "When you post a listing, it will be displayed here",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(bottom = 60.dp)
                            )
                        }
                    )
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
                        .size(25.dp)
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
