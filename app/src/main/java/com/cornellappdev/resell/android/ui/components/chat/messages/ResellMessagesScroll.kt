package com.cornellappdev.resell.android.ui.components.chat.messages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.util.getRelativeTimeSpan


@Composable
fun ResellMessagesScroll(
    chats: List<BuyerSellerData>,
    onChatPressed: (BuyerSellerData) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    paddedTop: Dp = 0.dp,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            bottom = 100.dp,
            top = paddedTop,
        ),
        modifier = modifier,
    ) {
        items(items = chats) { item ->
            MessageCard(
                imageUrl = item.image,
                seller = item.name,
                title = item.item.toListing().title,
                message = item.recentMessage,
                unread = !item.viewed,
                relativeTimestamp = getRelativeTimeSpan(item.recentMessageTime)
            ) {
                onChatPressed(item)
            }

        }
    }
}
