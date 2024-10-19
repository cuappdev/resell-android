package com.cornellappdev.resell.android.ui.components.global.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.global.ResellTag
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun MessageTag(
    text: String,
    modifier: Modifier = Modifier,
    unreads: Int,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    val borderWidth = animateFloatAsState(
        targetValue = if (active) 2f else -1f,
        label = "tag border width"
    )
    val border = BorderStroke(
        width = borderWidth.value.dp,
        brush = animateResellBrush(
            targetGradient = active,
            start = Offset(100f, 100f),
            end = Offset(0f, 0f),
            animationSpec = tween(300),
        )
    )

    Row(
        modifier = modifier
            .border(border, RoundedCornerShape(999.dp))
            .padding(end = if (unreads != 0) 12.dp else 0.dp)
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
                .padding(top = 12.dp, start = 12.dp, bottom = 12.dp)
                .padding(end = if (unreads != 0) 6.dp else 12.dp),
            style = Style.title3,
            text = text
        )
        if (unreads != 0) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(125.dp))
                    .background(color = Color.Red)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = (6).dp, vertical = (0.41).dp),
                    style = Style.title3,
                    color = Color.White,
                    text = unreads.toString()
                )
            }
        }
    }
}

@Preview
@Composable
private fun TagPreview() {
    var toggle by remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(8.dp)) {
        MessageTag(
            text = "active",
            active = true,
            unreads = 0,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        MessageTag (
            text = "inactive",
            active = false,
            unreads = 0,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        MessageTag(
            text = "toggleable",
            active = toggle,
            unreads = 0,
            onClick = { toggle = !toggle }
        )

        Spacer(modifier = Modifier.padding(8.dp))

        MessageTag(
            text = "toggleable",
            active = toggle,
            unreads = 5,
            onClick = { toggle = !toggle }
        )
    }
}