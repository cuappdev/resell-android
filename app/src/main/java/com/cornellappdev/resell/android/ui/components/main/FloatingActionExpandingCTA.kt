package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.BrushIcon
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun FloatingActionExpandingCTA(
    painter: Painter,
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animationState =
        animateFloatAsState(targetValue = if (expanded) 1f else 0f, label = "expand cta")
    val resellBrush = animateResellBrush(targetGradient = true, start = Offset(100f, 100f))
    val iconBrush = animateResellBrush(targetGradient = true, start = Offset(32f, 32f))

    if (animationState.value == 0f) return

    Surface(
        shape = CircleShape,
        border = BorderStroke(width = 3.dp, brush = resellBrush),
        modifier = modifier
            .alpha(animationState.value),
        color = Color.White,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp),
        ) {
            BrushIcon(
                brush = iconBrush,
                painter = painter,
                modifier = Modifier.padding(start = 16.dp, end = 12.dp)
            )

            Text(
                text = text,
                style = Style.title2Gradient,
                modifier = Modifier
                    .padding(end = 16.dp),
                softWrap = false,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun FloatingActionExpandingCTAPreview() {
    var expanded by remember { mutableStateOf(false) }

    FloatingActionExpandingCTA(
        painter = painterResource(id = R.drawable.ic_wishlist_small),
        text = "Explore",
        expanded = expanded,
        onClick = { expanded = !expanded }
    )
}
