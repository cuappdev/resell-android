package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfilePictureView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    // TODO Improve loading animation & shimmer
    AsyncImage(
        model = imageUrl,
        contentDescription = "pfp",
        modifier = modifier
            .sizeIn(minWidth = 31.dp, minHeight = 31.dp)
            .clip(CircleShape)
    )
}
