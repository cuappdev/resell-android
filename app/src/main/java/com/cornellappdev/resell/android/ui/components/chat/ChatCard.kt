package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.components.global.ResellCard

@Composable
fun ChatCard(imageUrl: String?) {
    ResellCard(
        imageUrl = imageUrl ?: "",
        title = "Richie",
        price = "$10.00",
        modifier = Modifier.padding(),
        onClick = {}
    )
}
