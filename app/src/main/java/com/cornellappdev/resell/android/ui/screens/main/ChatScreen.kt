package com.cornellappdev.resell.android.ui.screens.main

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.chat.ChatTag
import com.cornellappdev.resell.android.ui.components.chat.ResellChatScroll
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.singlePhotoPicker
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState = chatViewModel.collectUiStateValue()
    val listState = rememberLazyListState()

    LaunchedEffect(chatUiState.scrollBottom) {
        chatUiState.scrollBottom?.consumeSuspend {
            val history = chatUiState.currentChat.asSuccessOrNull()?.data?.chatHistory
            if (history?.isNotEmpty() == true) {
                // I put an empty item at the end of the list, so scroll to that.
                listState.animateScrollToItem(
                    history.size
                )
            }
        }
    }

    when (chatUiState.currentChat) {
        is ResellApiResponse.Pending -> {
            // TODO
        }

        is ResellApiResponse.Error -> {
            // TODO
        }

        is ResellApiResponse.Success -> {
            ChatLoadedContent(
                chat = chatUiState.currentChat.data,
                listState = listState,
                onBackPressed = chatViewModel::onBackPressed,
                chatUiState = chatUiState,
                onSend = chatViewModel::onSendMessage,
                onTextChange = chatViewModel::onTyped,
                onNegotiatePressed = chatViewModel::onNegotiatePressed,
                onVenmoPressed = chatViewModel::payWithVenmoPressed,
                showPayWithVenmo = chatUiState.showPayWithVenmo,
                showNegotiate = chatUiState.showNegotiate,
                onSendAvailability = chatViewModel::onSendAvailabilityPressed,
                onImageUpload = chatViewModel::onImageSelected,
                onPostClicked = chatViewModel::onPostClicked,
                onAvailabilityClicked = { availability, isSelf ->
                    chatViewModel.onAvailabilitySelected(
                        availability = availability,
                        isSelf = isSelf
                    )
                },
                onMeetingStateClicked = chatViewModel::onMeetingStateClicked,
                confirmedMeeting = chatUiState.confirmedMeeting,
                onViewAvailabilityPressed = chatViewModel::onViewOtherAvailabilityPressed
            )
        }
    }
}

@Composable
private fun ChatLoadedContent(
    chatUiState: ChatViewModel.MessagesUiState,
    chat: Chat,
    listState: LazyListState,
    onBackPressed: () -> Unit,
    onNegotiatePressed: () -> Unit,
    onMeetingStateClicked: (MeetingInfo, Boolean) -> Unit,
    onAvailabilityClicked: (AvailabilityDocument, Boolean) -> Unit,
    onViewAvailabilityPressed: () -> Unit,
    onSendAvailability: () -> Unit,
    onImageUpload: (Uri) -> Unit,
    onVenmoPressed: () -> Unit,
    onSend: (String) -> Unit,
    onTextChange: (String) -> Unit,
    showPayWithVenmo: Boolean,
    showNegotiate: Boolean,
    onPostClicked: (Post) -> Unit,
    confirmedMeeting: MeetingInfo?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChatHeader(
            onBackPressed = { onBackPressed() },
            confirmedMeeting = confirmedMeeting,
            otherName = chatUiState.otherName,
            title = chatUiState.title,
            onViewPressed = {
                if (confirmedMeeting != null) {
                    // For confirmed, doesn't matter who sent it.
                    onMeetingStateClicked(confirmedMeeting, false)
                }
            }
        )
        ResellChatScroll(
            chatClusters = chat.chatHistory,
            listState = listState,
            modifier = Modifier.weight(1f),
            onPostClicked = onPostClicked,
            onAvailabilityClicked = onAvailabilityClicked,
            onMeetingStateClicked = onMeetingStateClicked
        )
        ChatFooter(
            chatType = chatUiState.chatType,
            modifier = Modifier.imePadding(),
            onNegotiatePressed = { onNegotiatePressed() },
            onSend = {
                onSend(it)
            },
            onTextChange = {
                onTextChange(it)
            },
            text = chatUiState.typedMessage,
            onVenmoPressed = onVenmoPressed,
            showPayWithVenmo = showPayWithVenmo,
            showNegotiate = showNegotiate,
            onSendAvailability = onSendAvailability,
            onImageUpload = onImageUpload,
            otherName = chatUiState.otherName,
            showViewAvailability = chatUiState.showViewAvailability,
            onViewAvailabilityPressed = {
                onViewAvailabilityPressed()
            }
        )
    }
}

@Composable
private fun ChatHeader(
    confirmedMeeting: MeetingInfo?,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    otherName: String,
    onViewPressed: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .statusBarsPadding()
            .drawBehind {
                val borderWidth = 1.dp.toPx()
                val y = size.height - borderWidth / 2
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = borderWidth
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
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
                    .clickableNoIndication { onBackPressed() }
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(top = 12.dp),
                    style = Style.heading3
                )

                Text(
                    text = otherName,
                    modifier = Modifier.padding(top = 4.dp),
                    style = Style.body2,
                    color = Secondary
                )
            }

        }
        Spacer(Modifier.height(20.dp))
        if (confirmedMeeting != null) {
            Row(
                modifier = Modifier
                    .background(ResellPurple)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "image",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Meeting Confirmed",
                        style = Style.title2.copy(color = Color.White)
                    )
                    Text(
                        text = confirmedMeeting.convertToMeetingString(),
                        style = Style.body2.copy(color = Color.White)
                    )
                }
                Text(
                    text = "View",
                    style = Style.title2.copy(color = Color.White),
                    modifier = Modifier
                        .clickableNoIndication { onViewPressed() }
                )
            }
        }

    }
}

@Composable
private fun ChatFooter(
    chatType: ChatViewModel.ChatType,
    modifier: Modifier = Modifier,
    onNegotiatePressed: () -> Unit,
    onVenmoPressed: () -> Unit,
    onSend: (String) -> Unit,
    text: String,
    onTextChange: (String) -> Unit,
    showPayWithVenmo: Boolean,
    showNegotiate: Boolean,
    showViewAvailability: Boolean,
    otherName: String,
    onViewAvailabilityPressed: () -> Unit,
    onSendAvailability: () -> Unit,
    onImageUpload: (Uri) -> Unit
) {
    // Get the system insets
    val insets = WindowInsets.systemBars.asPaddingValues()
    val sendScale by animateFloatAsState(
        targetValue = if (text.isNotEmpty()) 1f else 0f,
        label = "send"
    )

    val singlePhotoPicker = singlePhotoPicker {
        if (it != null) {
            onImageUpload(it)
        }
    }

    Column(
        modifier = modifier
            .padding(bottom = insets.calculateBottomPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            if (showViewAvailability) {
                ChatTag(
                    text = "View ${otherName}'s Availability",
                    active = true,
                    onClick = { onViewAvailabilityPressed() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            if (showNegotiate) {
                ChatTag(
                    text = "Negotiate",
                    active = true,
                    onClick = { onNegotiatePressed() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            ChatTag(
                text = "Send Availability",
                active = true,
                onClick = {
                    onSendAvailability()
                }
            )
            Spacer(modifier = Modifier.width(12.dp))

            if (showPayWithVenmo) {
                ChatTag(
                    text = "Pay with",
                    active = false,
                    onClick = { onVenmoPressed() },
                    venmo = true
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 20.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = "image",
                tint = Color.Gray,
                modifier = Modifier
                    .size(32.dp)
                    .clickableNoIndication {
                        singlePhotoPicker.launch(
                            input = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Wash)
                    .padding(12.dp)
                    .heightIn(min = 24.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = {
                        onTextChange(it)
                    },
                    visualTransformation = VisualTransformation.None,
                    textStyle = Style.body2,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(20.dp)
                        .heightIn(min = 20.dp, max = 172.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoIndication {
                            if (text.isNotBlank()) {
                                onSend(text)
                            }
                        }
                ) {
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(sendScale)
                            .align(Alignment.Center),
                        color = ResellPurple
                    ) {}
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_up),
                        contentDescription = "send",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(sendScale)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )
        }
    }
}
