package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.util.richieMessages

@Composable
fun ResellChatScroll(
    chatHistory: List<ChatMessageCluster> = listOf(richieMessages(5), richieMessages(5)),
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onPostClicked: (Post) -> Unit,
    onAvailabilityClicked: (AvailabilityDocument, isSelf: Boolean) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(end = 8.dp),
    ) {
        itemsIndexed(chatHistory) { i, cluster ->
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                ChatMessage(
                    imageUrl = if (cluster.fromUser) null else cluster.senderImage,
                    messages = cluster.messages,
                    messageSender = { str, func, i ->
                        if (cluster.fromUser) UserMessage(func) else OtherMessage(str, func, i ?: 0)
                    },
                    onPostClicked = onPostClicked,
                    senderName = cluster.senderName ?: "",
                    onAvailabilityClicked = {
                        onAvailabilityClicked(
                            it,
                            cluster.fromUser
                        )
                    }
                )
                if (i != chatHistory.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        item {
            // Empty item to allow scroll to bottom.
        }
    }
}
