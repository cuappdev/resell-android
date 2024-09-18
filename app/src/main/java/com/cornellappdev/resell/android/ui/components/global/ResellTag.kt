package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun ResellTag(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    val borderWidth = animateFloatAsState(
        targetValue = if (active) 2f else 1f,
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

    Surface(
        shape = RoundedCornerShape(999.dp),
        border = border,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        },
        color = Color.White
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            style = Style.title3,
            text = text
        )
    }
}

@Preview
@Composable
private fun TagPreview() {
    var toggle by remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(8.dp)) {
        ResellTag(
            text = "active",
            active = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        ResellTag(
            text = "inactive",
            active = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        ResellTag(
            text = "toggleable",
            active = toggle,
            onClick = { toggle = !toggle }
        )
    }
}
