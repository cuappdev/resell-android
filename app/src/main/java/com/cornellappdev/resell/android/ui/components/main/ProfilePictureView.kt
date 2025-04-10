package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellAsyncImage
import com.cornellappdev.resell.android.ui.theme.ResellPreview


@Composable
fun ProfilePictureView(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {

    ResellAsyncImage(
        model = imageUrl,
        contentDescription = "pfp",
        modifier = modifier
            .sizeIn(minWidth = 31.dp, minHeight = 31.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        previewImageResource = R.drawable.ic_empty_pfp,
    )
}

@Preview
@Composable
fun ProfilePictureViewPreview() = ResellPreview {
    ProfilePictureView("https://core-docs.s3.amazonaws.com/murray_county_central_schools_ar/article/image/66f00638-2227-4795-84cc-8cefe4e4fb75.png")
}