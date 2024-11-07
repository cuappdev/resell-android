package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.profile.ProfileEmptyState
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.RequestMatchesViewModel
import kotlinx.coroutines.launch

@Composable
fun RequestMatchesScreen(
    requestMatchesViewModel: RequestMatchesViewModel = hiltViewModel(),
) {
    val uiState = requestMatchesViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onExitPressed = requestMatchesViewModel::onExit
        )

        when (uiState.listings) {
            is ResellApiResponse.Pending -> {}

            is ResellApiResponse.Error -> {}

            is ResellApiResponse.Success -> {
                ResellListingsScroll(
                    listings = uiState.listings.data,
                    listState = listState,
                    onListingPressed = requestMatchesViewModel::onListingPressed,
                    emptyState = {
                        ProfileEmptyState(
                            title = "No matching listings",
                            body = "You'll be notified when someone lists something similar to your request!",
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

@Composable
private fun Header(
    onTopPressed: () -> Unit,
    onExitPressed: () -> Unit = {},
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
                .defaultHorizontalPadding()
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Saved",
                style = Style.heading1
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_exit),
                contentDescription = "exit",
                tint = Primary,
                modifier = Modifier
                    .size(25.dp)
                    .clickableNoIndication {
                        onExitPressed()
                    }
            )
        }

        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
