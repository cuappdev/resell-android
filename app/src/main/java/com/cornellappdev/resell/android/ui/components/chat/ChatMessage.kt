package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo

@Composable
fun ChatMessage(
    imageUrl: String? = null,
    messageSender: @Composable (String?, @Composable () -> Unit, Int?) -> Unit,
    messages: List<ChatMessageData>,
    onPostClicked: (Post) -> Unit,
    onAvailabilityClicked: (AvailabilityDocument) -> Unit,
    onMeetingStateClicked: (MeetingInfo) -> Unit,
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
                            { MessageImage(it.imageUrl) },
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
                            null,
                            { ChatAvailability(senderName) {
                                if (it.availability == null) return@ChatAvailability
                                onAvailabilityClicked(it.availability)
                            } },
                            messages.size - i - 1
                        )
                    }

                    MessageType.State -> {
                        if (it.meetingInfo == null) {
                            MessageMeetingState(
                                text = it.content,
                                enabled = true,
                                actionText = null,
                            )
                        }
                        else {
                            MessageMeetingState(
                                text = it.content,
                                actionText = it.meetingInfo.actionText,
                                onActionTextClicked = {
                                    onMeetingStateClicked(it.meetingInfo)
                                },
                                icon = painterResource(id = it.meetingInfo.icon),
                                enabled = it.meetingInfo.mostRecent
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
fun UserMessage(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        content()
    }
}

@Composable
fun OtherMessage(imageUrl: String?, content: @Composable () -> Unit, pos: Int = 0) {
    Row {
        if (imageUrl != null && pos == 0) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(32.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(32.dp)),
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop
            )
        } else {
            Spacer(modifier = Modifier.width(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        content()
    }
}
