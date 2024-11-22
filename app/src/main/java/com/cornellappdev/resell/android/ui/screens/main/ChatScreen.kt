package com.cornellappdev.resell.android.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.messages.ChatTag
import com.cornellappdev.resell.android.ui.components.global.messages.ResellChatScroll
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState = chatViewModel.collectUiStateValue()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Chat().chatHistory) {
        coroutineScope.launch {
            if (Chat().chatHistory.isNotEmpty()) {
                listState.animateScrollToItem(Chat().chatHistory.size - 1)
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
                onSyncCalendarPressed = chatViewModel::onSyncToCalendarPressed,
                chatUiState = chatUiState,
                onSend = chatViewModel::onSendMessage
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
    onSyncCalendarPressed: () -> Unit,
    onSend: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ChatHeader(
            chat = chat,
            onBackPressed = { onBackPressed() },
            confirmedMeeting = true,
            sellerName = chatUiState.sellerName,
            title = chatUiState.title
        )
        ResellChatScroll(
            chatHistory = chat.chatHistory,
            listState = listState,
            modifier = Modifier.weight(1f)
        )
        ChatFooter(
            chatType = chatUiState.chatType,
            modifier = Modifier.imePadding(),
            onNegotiatePressed = { onSyncCalendarPressed() },
            onSend = {
                onSend(it)
            }
        )
    }
}

@Composable
private fun ChatHeader(
    chat: Chat,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    confirmedMeeting: Boolean,
    title: String,
    sellerName: String
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
                    text = sellerName,
                    modifier = Modifier.padding(top = 4.dp),
                    style = Style.body2,
                    color = Secondary
                )
            }

        }
        Spacer(Modifier.height(20.dp))
        if (confirmedMeeting) {
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
                        text = "October 23, 1:30 PM",
                        style = Style.body2.copy(color = Color.White)
                    )
                }
                Text(
                    text = "View",
                    style = Style.title2.copy(color = Color.White)
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
    onSend: (String) -> Unit
) {
    // TODO move to VM
    var text by remember { mutableStateOf("") }

    val sendScale by animateFloatAsState(
        targetValue = if (text.isNotEmpty()) 1f else 0f,
        label = "send"
    )
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
        ) {
            ChatTag(
                text = "Negotiate",
                active = true,
                onClick = { onNegotiatePressed() }
            )
            if (chatType == ChatViewModel.ChatType.Purchases) {
                Spacer(modifier = Modifier.width(12.dp))
                ChatTag(
                    text = "Send Availability",
                    active = true,
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(12.dp))
                ChatTag(
                    text = "Pay with",
                    active = false,
                    onClick = {},
                    venmo = true
                )
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
                    .clickableNoIndication {}
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
                    onValueChange = { text = it },
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
                                text = ""
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
