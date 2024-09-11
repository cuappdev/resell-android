package com.cornellappdev.android.resell.ui.components.global

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.resell.ui.theme.ResellGradientDiagonal
import com.cornellappdev.android.resell.ui.theme.Stroke
import com.cornellappdev.android.resell.ui.theme.Style

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    val border = if (active) {
        BorderStroke(
            width = 2.dp,
            brush = ResellGradientDiagonal,
        )
    } else {
        BorderStroke(
            width = 1.dp,
            brush = SolidColor(Stroke),
        )
    }

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
    }

}
