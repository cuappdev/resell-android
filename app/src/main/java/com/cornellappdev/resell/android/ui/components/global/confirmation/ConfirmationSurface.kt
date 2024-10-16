package com.cornellappdev.resell.android.ui.components.global.confirmation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style.title2

@Composable
fun ConfirmationSurface(
    icon: Painter? = null,
    color: Color = ResellPurple,
    title: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    Surface(
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 12.dp,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon, contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                }

                Text(
                    text = title,
                    style = title2,
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmationSurfacePreview() = ResellPreview(padding = 8.dp) {
    ConfirmationSurface(
        icon = null,
        color = ResellPurple,
        title = "Ravina.shop has been blocked.",
        onDismiss = {}
    )

    Spacer(modifier = Modifier.height(24.dp))

    ConfirmationSurface(
        icon = painterResource(id = R.drawable.ic_info),
        color = Secondary,
        title = "Something went wrong with your request. Please try again.",
        onDismiss = {}
    )
}
