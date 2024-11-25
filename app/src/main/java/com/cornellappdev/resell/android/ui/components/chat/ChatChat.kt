package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

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
