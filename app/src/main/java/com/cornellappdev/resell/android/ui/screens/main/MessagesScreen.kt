package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.messages.MessageTag
import com.cornellappdev.resell.android.ui.components.global.messages.ResellMessageScroll
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    messagesViewModel: ChatViewModel = hiltViewModel(),
) {
    val chatUiState = messagesViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        MessagesHeader(
            activeChat = chatUiState.chatType,
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onChatTypePressed = messagesViewModel::onChangeChatType
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if(chatUiState.filteredChats.isEmpty()) 128.dp else 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (chatUiState.filteredChats.isEmpty()) {
                val isOffers = chatUiState.chatType == ChatViewModel.ChatType.Offers
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = "No messages with ${if(isOffers) "buyers" else "sellers"} yet",
                    style = Style.heading2
                )
                Spacer(modifier = Modifier.height(19.dp))
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = if(isOffers) "When a buyer contacts you, you'll see their messages here"
                            else "When you contact a seller, you'll see your messages here",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    style = Style.body1
                )
            } else {
                ResellMessageScroll(
                    chats = chatUiState.filteredChats,
                    onChatPressed = {
                        messagesViewModel.onMessagePressed(it)
                    },
                    listState = listState,
                )
            }
        }
    }
}

@Composable
private fun MessagesHeader(
    activeChat: ChatViewModel.ChatType,
    onTopPressed: () -> Unit,
    onChatTypePressed: (ChatViewModel.ChatType) -> Unit = {},
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
                text = "Messages",
                style = Style.heading1
            )
        }

        Spacer(modifier = Modifier.height(Padding.medium))

        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.medium, Alignment.Start)
        ) {
            Spacer(modifier = Modifier.size(Padding.medium))
            MessageTag(
                text = "Purchases",
                active = activeChat == ChatViewModel.ChatType.Purchases,
                unreads = 0,
                onClick = { onChatTypePressed(ChatViewModel.ChatType.Purchases) }
            )
            MessageTag(
                text = "Offers",
                active = activeChat == ChatViewModel.ChatType.Offers,
                unreads = 12,
                onClick = { onChatTypePressed(ChatViewModel.ChatType.Offers) }
            )
        }
        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
