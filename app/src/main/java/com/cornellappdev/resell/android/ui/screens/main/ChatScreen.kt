package com.cornellappdev.resell.android.ui.screens.main

import com.cornellappdev.resell.android.ui.components.global.messages.ChatMessage
import com.cornellappdev.resell.android.ui.components.global.messages.OtherMessage
import com.cornellappdev.resell.android.ui.components.global.messages.UserMessage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.ui.components.global.messages.ChatTag
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.richieMessages
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    messagesViewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState = messagesViewModel.collectUiStateValue()
    var text by remember { mutableStateOf("") }
    val insets = WindowInsets.isImeVisible
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (insets) 24.dp else 0.dp, top=if (insets) 304.dp else 0.dp),
    ) {
        ChatHeader (
            chat = chatUiState.currentChat ?: Chat(chatId = -1) ,
            onBackPressed = { messagesViewModel.onBackPressed() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(Color.White)
                .zIndex(1f)
        )
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(12.dp)
                .padding(end = 8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(128.dp)) }
            item {
                ChatMessage(
                    imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                    messages = richieMessages(5),
                    messageSender = { str, funct, i ->
                        OtherMessage(str, funct, i ?: 0)
                    }
                )
            }
            item {Spacer(modifier = Modifier.height(12.dp))}
            item {
                ChatMessage(
                    imageUrl = null,
                    messageSender = { str, funct, i ->
                        UserMessage(funct)
                    },
                    messages = richieMessages(5)
                )
            }
            item {
                Spacer(modifier = Modifier.height(128.dp))
            }
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            ) {
                ChatTag (
                    text = "Negotiate",
                    active = true,
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(12.dp))
                ChatTag (
                    text = "Send Availability",
                    active = true,
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(12.dp))
                ChatTag (
                    text = "Pay with",
                    active = false,
                    onClick = {},
                    venmo = true
                )
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
                        .clickableNoIndication{}
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = Style.body2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(48.dp)
                        .heightIn(min = 48.dp, max = 200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Wash)
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier
                    .width(12.dp)
                )
            }
        }

    }
}

@Composable
private fun ChatHeader(
    chat: Chat,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
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
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                    strokeWidth = borderWidth
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "back",
                modifier = Modifier
                    .padding(top = 20.dp, start = 12.dp)
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickableNoIndication{ onBackPressed() }
            )
            Column (
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
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
    }
}