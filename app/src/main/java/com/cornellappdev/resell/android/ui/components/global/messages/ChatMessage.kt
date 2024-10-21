package com.cornellappdev.resell.android.ui.components.global.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.ui.components.global.ResellCard
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

@Composable
fun ChatMessage(
    imageUrl: String? = null,
    messageSender: @Composable (String?, @Composable () -> Unit, Int?) -> Unit,
    messages: List<ChatMessageData>,

    ) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            messages.forEachIndexed { i, it ->
                when (it.messageType) {
                    MessageType.Image -> {
                        messageSender(imageUrl, { MessageImage(it.content) }, messages.size - i - 1)
                    }

                    MessageType.Card -> {
                        messageSender(imageUrl, { MessageCard(it.content) }, messages.size - i - 1)
                    }

                    MessageType.Message ->
                        messageSender(
                            imageUrl,
                            {
                                MessageChat(
                                    it.content,
                                    if (imageUrl == null) Color.White else null
                                )
                            },
                            messages.size - i - 1
                        )
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

@Composable
fun MessageChat(text: String, color: Color?) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (color != null) ResellPurple else Wash)
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = Style.body2.copy(color = color ?: Style.body2.color),
            modifier = Modifier
                .heightIn(min = 19.dp)
                .widthIn(max = 224.dp)
        )
    }
}

@Composable
fun MessageImage(imageUrl: String?) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .heightIn(220.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = painterResource(id = R.drawable.ic_launcher_background),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MessageCard(imageUrl: String?) {
    ResellCard(
        imageUrl = imageUrl ?: "",
        title = "Richie",
        price = "$10.00",
        modifier = Modifier.padding(),
        onClick = {}
    )
}

