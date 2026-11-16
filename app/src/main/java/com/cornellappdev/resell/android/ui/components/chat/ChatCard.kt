package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.components.global.ImageState
import com.cornellappdev.resell.android.ui.components.global.ResellCard

@Composable
fun ChatCard(
    image: ImageState,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ResellCard(
        image = image,
        title = title,
        price = price,
        modifier = modifier.padding(),
        onClick = onClick
    )
}
