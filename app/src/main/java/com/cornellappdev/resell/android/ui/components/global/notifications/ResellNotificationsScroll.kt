package com.cornellappdev.resell.android.ui.components.global.notifications

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.cornellappdev.resell.android.model.messages.Notification
import com.cornellappdev.resell.android.ui.components.global.messages.NotificationCard
import com.cornellappdev.resell.android.ui.components.global.messages.SwipeableNotificationCard
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.viewmodel.main.NotificationsHubViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResellNotificationsScroll(
    unreadNotifications : List<Notification>,
    weekNotifications : List<Notification>,
    monthNotifications : List<Notification>,
    otherNotifications : List<Notification>,
    onNotificationPressed: (Notification) -> Unit,
    onNotificationArchived: (Notification) -> Unit,
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
                    if (notification.unread) {
                        SwipeableNotificationCard(
                            notification = notification,
                            imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                            title = notification.title,
                            timestamp = notification.timestamp,
                            unread = notification.unread,
                            onArchive = {
                                notification.unread = false
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
                weekNotifications.forEach {
                    NotificationCard(
                        imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                        title = it.title,
                        timestamp = it.timestamp,
                        unread = it.unread,
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
                        imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                        title = it.title,
                        timestamp = it.timestamp,
                        unread = it.unread,
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
                        imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                        title = it.title,
                        timestamp = it.timestamp,
                        unread = it.unread,
                    ) {
                        onNotificationPressed(it)
                    }
                }
            }
        }
    }
}
