package com.cornellappdev.resell.android.ui.components.global.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun ChatTag(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    venmo: Boolean = false,
    onClick: () -> Unit,
) {
    val border = BorderStroke(
        width = 1.dp,
        brush = animateResellBrush(
            targetGradient = active,
            start = Offset(100f, 100f),
            end = Offset(0f, 0f),
        )
    )

    Row(
        modifier = modifier
            .border(border, RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null

            ) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, top = 10.dp, bottom = 10.dp, end = if(venmo) 0.dp else 8.dp),
            style = Style.title4,
            text = text
        )
        if(venmo){
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, end = 8.dp),
                style = Style.title4.copy(color = Color(0xFF3D8AF7), fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic),
                text = " venmo"
            )
        }
    }
}