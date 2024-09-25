package com.cornellappdev.resell.android.ui.screens.main

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellTabBar
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = profileViewModel.collectUiStateValue()

    val staggeredState = rememberLazyStaggeredGridState()

    // TODO: bare bones right now
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
            onTabSelected = { profileViewModel.onTabSelected(it) },
        )

        AnimatedContent(
            targetState = uiState.profileTab,
            label = "profile content",
            transitionSpec = simpleFadeInOut,
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            if (tab == ProfileViewModel.ProfileTab.SHOP
                || tab == ProfileViewModel.ProfileTab.ARCHIVE
            ) {
                ResellListingsScroll(
                    listings = uiState.listings,
                    onListingPressed = { profileViewModel.onListingPressed(it) },
                    listState = staggeredState,
                    paddedTop = 24.dp,
                )
            } else {
                // TODO: Wishlist
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "pfp",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopCenter)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "settings",
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp)
                    .defaultHorizontalPadding()
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
                    modifier = Modifier.size(25.dp)
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
            style = Style.body2
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
