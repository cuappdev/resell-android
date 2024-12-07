package com.cornellappdev.resell.android.ui.screens.main

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.Chat
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
    messagesViewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState = messagesViewModel.collectUiStateValue()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Chat(chatId = -1).chatHistory) {
        coroutineScope.launch {
            if (Chat(chatId = -1).chatHistory.isNotEmpty()) {
                listState.animateScrollToItem(Chat(chatId = -1).chatHistory.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ChatHeader(
            chat = chatUiState.currentChat ?: Chat(chatId = -1),
            onBackPressed = { messagesViewModel.onBackPressed() },
            confirmedMeeting = true
        )
        ResellChatScroll(
            chatHistory = chatUiState.currentChat?.chatHistory ?: Chat(chatId = -1).chatHistory,
            listState = listState,
            modifier = Modifier.weight(1f)
        )
        ChatFooter(
            chatType = chatUiState.chatType,
            modifier = Modifier.imePadding(),
            onNegotiatePressed = { messagesViewModel.onSyncToCalendarPressed() }
        )
    }
}

@Composable
private fun ChatHeader(
    chat: Chat,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    confirmedMeeting: Boolean

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
                    text = chat.title,
                    modifier = Modifier.padding(top = 12.dp),
                    style = Style.heading3
                )

                Text(
                    text = chat.seller,
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
    onNegotiatePressed: () -> Unit
) {
    var text by remember { mutableStateOf("") }
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

                if (text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(ResellPurple)
                            .clickableNoIndication { }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_up),
                            contentDescription = "send",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )
        }
    }
}