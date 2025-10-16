package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.TransactionInfo
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.ResellPreview

@Composable
fun ChatMessageCluster(
    imageUrl: String? = null,
    messageSender: @Composable (String?, @Composable () -> Unit, Int?) -> Unit,
    messages: List<ChatMessageData>,
    onPostClicked: (Post) -> Unit,
    onAvailabilityClicked: (AvailabilityDocument) -> Unit,
    onMeetingStateClicked: (MeetingInfo) -> Unit,
    onTransactionStateClicked: (TransactionInfo) -> Unit,
    senderName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            messages.forEachIndexed { i, it ->
                when (it.messageType) {
                    MessageType.Image -> {
                        messageSender(
                            imageUrl,
                            { ChatImage(it.imageUrl) },
                            messages.size - i - 1
                        )
                    }

                    MessageType.Card -> {
                        if (it.post != null) {
                            messageSender(imageUrl, {
                                ChatCard(
                                    imageUrl = it.post.images.getOrNull(0) ?: "",
                                    title = it.post.title,
                                    price = it.post.toListing().price,
                                    modifier = Modifier.width(200.dp),
                                    onClick = {
                                        onPostClicked(it.post)
                                    }
                                )
                            }, messages.size - i - 1)
                        }
                    }

                    MessageType.Message ->
                        messageSender(
                            imageUrl,
                            {
                                ChatChat(
                                    text = it.content,
                                    self = imageUrl == null,
                                    timestamp = it.timestampString
                                )
                            },
                            messages.size - i - 1
                        )

                    MessageType.Availability -> {
                        messageSender(
                            imageUrl,
                            {
                                ChatAvailability(senderName) {
                                    if (it.availability == null) return@ChatAvailability
                                    onAvailabilityClicked(it.availability)
                                }
                            },
                            messages.size - i - 1
                        )
                    }

                    MessageType.State -> {
                        if (it.meetingInfo == null && it.transactionInfo == null) {
                            ChatMeetingState(
                                text = it.content,
                                enabled = true,
                                actionText = null,
                            )
                        } else if (it.meetingInfo != null) {
                            ChatMeetingState(
                                text = it.content,
                                actionText = it.meetingInfo.actionText,
                                onActionTextClicked = {
                                    onMeetingStateClicked(it.meetingInfo)
                                },
                                icon = painterResource(id = it.meetingInfo.icon),
                                enabled = it.meetingInfo.mostRecent
                            )
                        } else if (it.transactionInfo != null) {
                            ChatTransactionState(
                                text = it.content,
                                actionText = it.transactionInfo.actionText,
                                onActionTextClicked = {
                                    onTransactionStateClicked(it.transactionInfo)
                                },
                                icon = painterResource(id = it.transactionInfo.icon),
                                enabled = it.transactionInfo.mostRecent
                            )
                        }
                    }
                }
                if (i != messages.size - 1) Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun MyMessage(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        content()
    }
}

@Composable
fun OtherMessage(imageUrl: String?, content: @Composable () -> Unit, pos: Int = 0) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        if (imageUrl != null && pos == 0) {
            ProfilePictureView(
                imageUrl = imageUrl,
                modifier = Modifier
                    .size(32.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Preview
@Composable
private fun OtherMessagePreview() = ResellPreview {
    OtherMessage(
        imageUrl = "",
        content = {
            ChatAvailability(sender = "caleb") { }
        },
        pos = 0
    )

    OtherMessage(
        imageUrl = "",
        content = {
            ChatChat(text = "Hello", timestamp = "12:00 PM", self = false)
        },
        pos = 0
    )
}
