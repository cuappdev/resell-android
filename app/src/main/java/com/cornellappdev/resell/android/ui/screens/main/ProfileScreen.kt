package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.animation.AnimatedContent
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
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.profile.ProfileEmptyState
import com.cornellappdev.resell.android.ui.components.profile.ProfileHeader
import com.cornellappdev.resell.android.ui.components.profile.ResellRequestsScroll
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.viewmodel.main.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
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
            selectedTab = uiState.profileTab,
            rightIcon = R.drawable.ic_settings,
            onTabSelected = { profileViewModel.onTabSelected(it) },
            onRightPressed = { profileViewModel.onSettingsPressed() },
        )

        AnimatedContent(
            targetState = uiState.profileTab,
            label = "profile content",
            transitionSpec = simpleFadeInOut,
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                ProfileViewModel.ProfileTab.SHOP -> {
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

                ProfileViewModel.ProfileTab.ARCHIVE -> {
                    when (uiState.archiveListings) {
                        is ResellApiResponse.Pending -> {

                        }

                        is ResellApiResponse.Error -> {

                        }

                        is ResellApiResponse.Success -> {
                            ResellListingsScroll(
                                listings = uiState.archiveListings.data,
                                onListingPressed = { profileViewModel.onListingPressed(it) },
                                listState = staggeredState,
                                paddedTop = 12.dp,
                                emptyState = {
                                    ProfileEmptyState(
                                        title = "No listings archived",
                                        body = "When you archive a listing, it will be displayed here",
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(bottom = 60.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                ProfileViewModel.ProfileTab.WISHLIST -> {
                    when (uiState.requests) {
                        is ResellApiResponse.Pending -> {
                        }

                        is ResellApiResponse.Error -> {
                        }

                        is ResellApiResponse.Success -> {
                            ResellRequestsScroll(
                                requests = uiState.requests.data,
                                onClick = {
                                    profileViewModel.onRequestPressed(
                                        uiState.requests.data[it]
                                    )
                                },
                                onDelete = {
                                    profileViewModel.onRequestDeletePressed(
                                        uiState.requests.data[it]
                                    )
                                },
                                emptyState = {
                                    ProfileEmptyState(
                                        title = "No active requests",
                                        body = "Submit a request and get notified when someone lists something similar",
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
        }
    }
}
