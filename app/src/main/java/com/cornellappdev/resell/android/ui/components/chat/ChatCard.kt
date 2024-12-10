package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.components.global.ResellCard

@Composable
fun ChatCard(
    imageUrl: String?,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ResellCard(
        imageUrl = imageUrl ?: "",
        title = title,
        price = price,
        modifier = modifier.padding(),
        onClick = onClick
    )
}
