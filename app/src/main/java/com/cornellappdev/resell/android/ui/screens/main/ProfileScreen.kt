package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellTabBar
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.instantFadeInOut
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
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
        val header = @Composable {
            ProfileHeader(
                imageUrl = uiState.imageUrl,
                shopName = uiState.shopName,
                vendorName = uiState.vendorName,
                bio = uiState.bio,
                selectedTab = uiState.profileTab,
                onTabSelected = { profileViewModel.onTabSelected(it) },
                onSettingsPressed = { profileViewModel.onSettingsPressed() },
                onSearchPressed = { profileViewModel.onSearchPressed() },
            )
        }

        AnimatedContent(
            targetState = uiState.profileTab,
            label = "profile content",
            transitionSpec = instantFadeInOut,
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                ProfileViewModel.ProfileTab.SHOP -> {
                    ResellListingsScroll(
                        listings = uiState.shopListings,
                        onListingPressed = { profileViewModel.onListingPressed(it) },
                        listState = staggeredState,
                        paddedTop = 12.dp,
                    ) {
                        header()
                    }
                }

                ProfileViewModel.ProfileTab.ARCHIVE -> {
                    ResellListingsScroll(
                        listings = uiState.archiveListings,
                        onListingPressed = { profileViewModel.onListingPressed(it) },
                        listState = staggeredState,
                        paddedTop = 12.dp,
                    ) {
                        header()
                    }
                }

                ProfileViewModel.ProfileTab.WISHLIST -> {
                    // TODO: Implement Wishlist view
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        header()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    imageUrl: String,
    shopName: String,
    vendorName: String,
    bio: String,
    selectedTab: ProfileViewModel.ProfileTab,
    onTabSelected: (ProfileViewModel.ProfileTab) -> Unit,
    onSettingsPressed: () -> Unit,
    onSearchPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ProfilePictureView(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopCenter),
                imageUrl = imageUrl,
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "settings",
                modifier = Modifier
                    .defaultHorizontalPadding()
                    .padding(top = 8.dp)
                    .size(25.dp)
                    .align(Alignment.TopStart)
                    .clickableNoIndication { onSettingsPressed() }
            )

            // TODO: Eventually should be `...` option.
            Row(
                modifier = Modifier
                    .defaultHorizontalPadding()
                    .padding(top = 8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "search",
                    modifier = Modifier
                        .size(25.dp)
                        .clickableNoIndication { onSearchPressed() }
                )
            }
        }

        Text(
            text = shopName,
            modifier = Modifier.padding(top = 12.dp),
            style = Style.heading3
        )

        Text(
            text = vendorName,
            modifier = Modifier.padding(top = 4.dp),
            style = Style.body2,
            color = Secondary
        )

        Text(
            text = bio,
            modifier = Modifier.padding(top = 12.dp),
            maxLines = 3,
            style = Style.body2,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        ResellTabBar(
            painterIds = listOf(
                R.drawable.ic_shop,
                R.drawable.ic_archive,
                R.drawable.ic_wishlist
            ),
            selectedPainter = selectedTab.ordinal,
        ) {
            onTabSelected(ProfileViewModel.ProfileTab.entries[it])
        }
    }
}
