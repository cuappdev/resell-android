package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyListState
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
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.chat.messages.MessageTag
import com.cornellappdev.resell.android.ui.components.chat.messages.ResellMessagesScroll
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import com.cornellappdev.resell.android.viewmodel.main.MessagesViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    messagesViewModel: MessagesViewModel = hiltViewModel(),
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
            onChatTypePressed = messagesViewModel::onChangeChatType,
            purchasesUnreads = chatUiState.purchasesUnreads,
            offersUnreads = chatUiState.offersUnreads
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (chatUiState.loadedEmpty) 128.dp else 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when (chatUiState.loadedState) {
                ResellApiState.Loading -> {
                    // TODO Loading State
                }

                ResellApiState.Error -> {
                    // TODO Error State
                }

                ResellApiState.Success -> {
                    LoadedContent(chatUiState, messagesViewModel, listState)
                }
            }

        }
    }
}

@Composable
private fun ColumnScope.LoadedContent(
    chatUiState: MessagesViewModel.MessagesUiState,
    messagesViewModel: MessagesViewModel,
    listState: LazyListState
) {
    if (chatUiState.filteredChats.asSuccess().data.isEmpty()) {
        val isOffers = chatUiState.chatType == ChatViewModel.ChatType.Offers
        Text(
            modifier = Modifier.Companion.align(alignment = Alignment.CenterHorizontally),
            text = "No messages with ${if (isOffers) "buyers" else "sellers"} yet",
            style = Style.heading2
        )
        Spacer(modifier = Modifier.height(19.dp))
        Text(
            modifier = Modifier.Companion.align(alignment = Alignment.CenterHorizontally),
            text = if (isOffers) "When a buyer contacts you, you'll see their messages here"
            else "When you contact a seller, you'll see your messages here",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            style = Style.body1
        )
    } else {
        ResellMessagesScroll(
            chats = chatUiState.filteredChats.asSuccess().data,
            onChatPressed = {
                messagesViewModel.onMessagePressed(it)
            },
            listState = listState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MessagesHeader(
    activeChat: ChatViewModel.ChatType,
    onTopPressed: () -> Unit,
    onChatTypePressed: (ChatViewModel.ChatType) -> Unit = {},
    purchasesUnreads: Int,
    offersUnreads: Int,
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
                unreads = purchasesUnreads,
                onClick = { onChatTypePressed(ChatViewModel.ChatType.Purchases) }
            )
            MessageTag(
                text = "Offers",
                active = activeChat == ChatViewModel.ChatType.Offers,
                unreads = offersUnreads,
                onClick = { onChatTypePressed(ChatViewModel.ChatType.Offers) }
            )
        }
        Spacer(modifier = Modifier.height(Padding.medium))
    }
}
