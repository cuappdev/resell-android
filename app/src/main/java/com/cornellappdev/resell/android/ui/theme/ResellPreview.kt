package com.cornellappdev.resell.android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ResellPreview(
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        content()
    }
}
