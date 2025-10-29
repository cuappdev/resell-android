package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun FromHistoryHeader(text: String, onBack: () -> Unit, onTopPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onTopPressed()
            }
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "back",
            modifier = Modifier
                .clickable(
                    onClick = onBack
                )
                .align(Alignment.CenterStart)
        )
        Text(
            text = text,
            style = Style.heading3,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun FromHistoryHeaderPreview() {
    FromHistoryHeader("From History", {}, {})
}