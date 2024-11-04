package com.cornellappdev.resell.android.ui.screens.externalprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.profile.ProfileEmptyState
import com.cornellappdev.resell.android.ui.components.profile.ProfileHeader
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalProfileViewModel

@Composable
fun ExternalProfileScreen(
    profileViewModel: ExternalProfileViewModel = hiltViewModel()
) {
    val uiState = profileViewModel.collectUiStateValue()
    val staggeredState = rememberLazyStaggeredGridState()

    Column(
        modifier = Modifier
            .fillMaxSize()
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
}
