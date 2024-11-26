package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

@Composable
fun ChatChat(
    text: String,
    timestamp: String,
    self: Boolean = false,
) {
    val color = if (self) Color.White else null
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (self) ResellPurple else Wash)
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = Style.body2.copy(color = color ?: Style.body2.color),
            modifier = Modifier
                .heightIn(min = 19.dp)
                .widthIn(max = 224.dp)
        )

        Text(
            text = timestamp,
            style = Style.subtitle1.copy(color = color ?: Style.body2.color),
            modifier = Modifier
                .align(
                    if (self) Alignment.End else Alignment.Start
                )
                .padding(top = 4.dp)
        )
    }
}

@Preview(apiLevel = 34)
@Composable
private fun MessageChatPreview() = ResellPreview(padding = 16.dp) {
    ChatChat(text = "Hello", timestamp = "12:00 PM", self = false)
    ChatChat(
        text = "dis one long ass message boy you better read about this in the history books baby",
        timestamp = "12:00 PM",
        self = true
    )
    ChatChat(
        text = "dis one long ass message boy you better read about this in the history books baby",
        timestamp = "12:00 PM",
        self = false
    )
}
