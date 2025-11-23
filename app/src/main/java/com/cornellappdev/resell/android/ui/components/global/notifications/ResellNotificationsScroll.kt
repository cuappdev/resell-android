package com.cornellappdev.resell.android.ui.components.global.notifications

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun ResellNotificationsScroll(
    unreadNotifications: List<InAppNotification>,
    weekNotifications: List<InAppNotification>,
    monthNotifications: List<InAppNotification>,
    otherNotifications: List<InAppNotification>,
    onNotificationPressed: (InAppNotification) -> Unit,
    onNotificationArchived: (InAppNotification) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier,
    ) {

        if (unreadNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "New",
                    style = Style.title1,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                val currUnreads by remember { mutableStateOf(unreadNotifications) }
                currUnreads.mapIndexed { i, notification ->
                    if (notification.isUnread) {
                        SwipeableNotificationCard(
                            notification = notification,
                            imageUrl = notification.user.photoUrl, //TODO: use other applicable image data once added to the backend
                            body = notification.body,
                            timestamp = notification.timeState,
                            unread = true,
                            onArchive = {
                                onNotificationArchived(notification)
                            },
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                        ) {
                            onNotificationPressed(notification)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (weekNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "Last 7 Days",
                    style = Style.title1,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                weekNotifications.forEach { it ->
                    NotificationCard(
                        imageUrl = it.user.photoUrl, //TODO: use other applicable image data once added to the backend
                        body = it.body,
                        timestamp = it.timeState,
                        unread = it.isUnread,
                    ) {
                        onNotificationPressed(it)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }


        if (monthNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "Last 30 Days",
                    style = Style.title1,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                monthNotifications.forEach {
                    NotificationCard(
                        imageUrl = it.user.photoUrl, //TODO: use other applicable image data once added to the backend
                        body = it.body,
                        timestamp = it.timeState,
                        unread = it.isUnread,
                    ) {
                        onNotificationPressed(it)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (otherNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "Older",
                    style = Style.title1,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                otherNotifications.forEach {
                    NotificationCard(
                        imageUrl = it.user.photoUrl, //TODO: use other applicable image data once added to the backend
                        body = it.body,
                        timestamp = it.timeState,
                        unread = it.isUnread,
                    ) {
                        onNotificationPressed(it)
                    }
                }
            }
        }
    }
}