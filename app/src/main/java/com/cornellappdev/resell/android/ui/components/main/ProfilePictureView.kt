package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.util.LocalInfiniteShimmer

@Composable
fun ProfilePictureView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    // TODO Improve loading animation & shimmer
    if (imageUrl.isEmpty()) {
        Surface(
            modifier = modifier
                .sizeIn(minWidth = 31.dp, minHeight = 31.dp)
                .clip(CircleShape),
            color = LocalInfiniteShimmer.current,
            shape = CircleShape,
        ) {}
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = "pfp",
            modifier = modifier
                .sizeIn(minWidth = 31.dp, minHeight = 31.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
