package com.cornellappdev.resell.android.ui.components.global.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.getRelativeTimeSpan

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
            item(key = "header_new") {
                Column {
                    Text(
                        text = "New",
                        style = Style.title1,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            items(
                items = unreadNotifications,
                key = { it.id }
            ) { notification ->
                SwipeableNotificationCard(
                    notification = notification,
                    imageUrl = notification.displayImageUrl,
                    body = notification.body,
                    timestamp = getRelativeTimeSpan(notification.createdInstant.toString()),
                    unread = true,
                    onArchive = {
                        onNotificationArchived(notification)
                    },
                    modifier = Modifier.animateItem()
                ) {
                    onNotificationPressed(notification)
                }
            }
            item(key = "spacer_new") {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (weekNotifications.isNotEmpty()) {
            item(key = "header_week") {
                Column {
                    Text(
                        text = "Last 7 Days",
                        style = Style.title1,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            items(
                items = weekNotifications,
                key = { it.id }
            ) { notification ->
                SwipeableNotificationCard(
                    notification = notification,
                    imageUrl = notification.displayImageUrl,
                    body = notification.body,
                    timestamp = getRelativeTimeSpan(notification.createdInstant.toString()),
                    unread = false,
                    onArchive = {
                        onNotificationArchived(notification)
                    },
                    modifier = Modifier.animateItem()
                ) {
                    onNotificationPressed(notification)
                }
            }
            item(key = "spacer_week") {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (monthNotifications.isNotEmpty()) {
            item(key = "header_month") {
                Column {
                    Text(
                        text = "Last 30 Days",
                        style = Style.title1,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            items(
                items = monthNotifications,
                key = { it.id }
            ) { notification ->
                SwipeableNotificationCard(
                    notification = notification,
                    imageUrl = notification.displayImageUrl,
                    body = notification.body,
                    timestamp = getRelativeTimeSpan(notification.createdInstant.toString()),
                    unread = false,
                    onArchive = {
                        onNotificationArchived(notification)
                    },
                    modifier = Modifier.animateItem()
                ) {
                    onNotificationPressed(notification)
                }
            }
            item(key = "spacer_month") {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (otherNotifications.isNotEmpty()) {
            item(key = "header_other") {
                Column {
                    Text(
                        text = "Older",
                        style = Style.title1,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            items(
                items = otherNotifications,
                key = { it.id }
            ) { notification ->
                SwipeableNotificationCard(
                    notification = notification,
                    imageUrl = notification.displayImageUrl,
                    body = notification.body,
                    timestamp = getRelativeTimeSpan(notification.createdInstant.toString()),
                    unread = false,
                    onArchive = {
                        onNotificationArchived(notification)
                    },
                    modifier = Modifier.animateItem()
                ) {
                    onNotificationPressed(notification)
                }
            }
        }
    }
}