package com.cornellappdev.resell.android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ResellPreview(
    padding: Dp = 0.dp,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    ResellTheme {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(padding)
        ) {
            content()
        }
    }
}
