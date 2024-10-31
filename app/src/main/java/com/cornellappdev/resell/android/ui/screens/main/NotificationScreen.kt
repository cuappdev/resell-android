package com.cornellappdev.resell.android.ui.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
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

        Column (
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
                                Log.d("Unread List Length", notificationsHubUiState.newNotifications.size.toString())
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
                text = "resell",
                style = Style.resellBrand
            )
            Row {
                Box(
                    modifier = Modifier
                        .clickableNoIndication { }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification_bell),
                        contentDescription = "notifications",
                        tint = Primary,
                        modifier = Modifier
                            .height(28.dp)
                            .width(30.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "search",
                    tint = Primary,
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }

        }

        // filters
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
