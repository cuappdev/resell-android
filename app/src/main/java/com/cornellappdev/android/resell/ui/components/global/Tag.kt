package com.cornellappdev.android.resell.ui.components.global

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.resell.ui.theme.Style
import com.cornellappdev.android.resell.ui.theme.animateResellBrush

@Composable
fun Tag(
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
        brush = animateResellBrush(active)
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        border = border,
        modifier = modifier,
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
        Tag(
            text = "active",
            active = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Tag(
            text = "inactive",
            active = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Tag(
            text = "toggleable",
            active = toggle,
            onClick = { toggle = !toggle }
        )
    }
}
