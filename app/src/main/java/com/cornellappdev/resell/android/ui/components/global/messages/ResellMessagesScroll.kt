package com.cornellappdev.resell.android.ui.components.global.messages

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
                imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                seller = item.name,
                title = item.listing.title,
                message = item.recentMessage,
                unread = true,
            ) {
                onChatPressed(item)
            }

        }
    }
}
