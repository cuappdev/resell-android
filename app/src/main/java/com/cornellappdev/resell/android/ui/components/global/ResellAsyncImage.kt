package com.cornellappdev.resell.android.ui.components.global

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.Companion.DefaultTransform
import com.cornellappdev.resell.android.R

/**
 * A wrapper around coil's AsyncImage, but contains an extra parameter, previewImageResource, which
 * allows us to specify what image should be used when the image is being viewed in a Preview. This
 * is useful because AsyncImages don't load in previews and it causes the previews to not look good.
 */
@Composable
fun ResellAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    @DrawableRes previewImageResource: Int = R.drawable.ic_image,
) {
    if (LocalInspectionMode.current) {
        Image(
            painterResource(previewImageResource),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            alpha = alpha,
        )
    } else {
        AsyncImage(
            model,
            contentDescription,
            modifier,
            transform,
            onState,
            alignment,
            contentScale,
            alpha,
        )
    }
}