package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.messages.NotificationType
import com.cornellappdev.resell.android.ui.components.global.ResellTag
import com.cornellappdev.resell.android.ui.components.global.notifications.ResellNotificationsScroll
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.viewmodel.main.NotificationsHubViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationsHubScreen(
    notificationsHubViewModel: NotificationsHubViewModel = hiltViewModel(),
) {
    val notificationsHubUiState = notificationsHubViewModel.collectUiStateValue()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotificationsHubHeader(
            notificationType = notificationsHubUiState.notificationType,
            onFilterPressed = notificationsHubViewModel::onToggleFilter,
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (notificationsHubUiState.categorizedNotifications.isEmpty()) 128.dp else 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when (notificationsHubUiState.loadedState) {
                is ResellApiState.Success -> {
                    if (notificationsHubUiState.categorizedNotifications.isEmpty()) {
                        Text(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            text = "You're all caught up",
                            style = Style.heading2
                        )
                        Spacer(modifier = Modifier.height(19.dp))
                        Text(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            text = "No new notifications right now",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = Style.body1
                        )
                    } else {
                        ResellNotificationsScroll(
                            notificationsHubUiState,
                            onNotificationPressed = {
                                notificationsHubViewModel.onNotificationPressed()
                            },
                            onNotificationArchived = {
                                notificationsHubViewModel.onNotificationArchived(it)
                            },
                            listState = listState,
                        )
                    }
                }

                is ResellApiState.Loading -> {}

                is ResellApiState.Error -> {}
            }
        }
    }
}

@Composable
private fun NotificationsHubHeader(
    notificationType: NotificationType?,
    onFilterPressed: (NotificationType?) -> Unit = {},
    onTopPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "back",
                modifier = Modifier
                    .padding(top = 20.dp, start = 12.dp)
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickableNoIndication { }
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Notifications",
                    modifier = Modifier.padding(top = 12.dp),
                    style = Style.heading3
                )
            }

        }
        Spacer(Modifier.height(20.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Padding.medium, Alignment.Start),
        ) {
            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }
            item {
                ResellTag(
                    text = "All",
                    active = notificationType == null,
                    onClick = { onFilterPressed(null) }
                )
            }
            items(items = NotificationType.entries) { filter ->
                ResellTag(
                    text = filter.name.lowercase().replaceFirstChar {
                        it.uppercase()
                    },
                    active = filter == notificationType,
                    onClick = { onFilterPressed(filter) }
                )
            }

            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }
        }

        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
