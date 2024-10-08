package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

/**
 * A default header framework used on many screens.
 */
@Composable
fun ResellHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leftPainter: Int? = null,
    rightPainter: Int? = null,
    onLeftClick: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null,
    bottomBar: Boolean = false,
) {
    Column(
        modifier = modifier.statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .defaultHorizontalPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (leftPainter != null) {
                Icon(
                    painter = painterResource(id = leftPainter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoIndication(onClick = onLeftClick ?: {}),
                    tint = Primary,
                )
            } else {
                Box(modifier = Modifier.size(24.dp)) {}
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = if (subtitle != null) Style.title1 else Style.heading3,
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = Style.title3,
                        color = AppDev
                    )
                }
            }

            if (rightPainter != null) {
                Icon(
                    painter = painterResource(id = rightPainter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoIndication(onClick = onRightClick ?: {}),
                    tint = Primary,
                )
            } else {
                Box(modifier = Modifier.size(24.dp)) {}
            }
        }

        Spacer(Modifier.height(12.dp))

        if (bottomBar) {
            Spacer(
                Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Stroke)
            )
        }
    }
}

@Preview
@Composable
private fun ResellHeaderPreview() = ResellPreview {
    ResellHeader(
        title = "Title",
        subtitle = "Subtitle",
        leftPainter = R.drawable.ic_exit,
        rightPainter = R.drawable.ic_plus,
        bottomBar = true,
    )

    ResellHeader(
        title = "Title",
        leftPainter = R.drawable.ic_archive,
        rightPainter = R.drawable.ic_settings,
    )

    ResellHeader(
        title = "Title",
        subtitle = "Subtitle",
        leftPainter = R.drawable.ic_archive,
    )

    ResellHeader(
        title = "Title",
        rightPainter = R.drawable.ic_settings,
    )
}
