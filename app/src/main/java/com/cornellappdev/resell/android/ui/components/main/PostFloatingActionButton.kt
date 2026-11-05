package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.BrushIcon
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun PostFloatingActionButton(
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val resellBrush = animateResellBrush(targetGradient = true, start = Offset(100f, 100f))
    val plusBrush = animateResellBrush(targetGradient = true)

    Surface(
        shape = CircleShape,
        border = BorderStroke(width = 3.dp, brush = resellBrush),
        modifier = modifier
            .size(67.dp),
        color = Color.White,
        onClick = onClick,
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            BrushIcon(
                painter = painterResource(id = R.drawable.ic_plus),
                brush = plusBrush,
                contentDescription = contentDescription,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview
@Composable
private fun PostFloatingActionButtonPreview() = ResellPreview(
    padding = 10.dp
) {
    PostFloatingActionButton(contentDescription = "New listing", onClick = {})
}
