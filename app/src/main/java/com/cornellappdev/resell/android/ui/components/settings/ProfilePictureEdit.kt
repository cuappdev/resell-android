package com.cornellappdev.resell.android.ui.components.settings

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPreview

@Composable
fun ProfilePictureEdit(
    imageBitmap: Bitmap?,
    onImageTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = imageBitmap,
            label = "pfp"
        ) { bitmap ->
            if (bitmap != null) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_empty_pfp),
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) {
                            onImageTapped()
                        },
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_edit_pfp),
            contentDescription = "camera",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onImageTapped()
                }
                .shadow(1.dp, CircleShape)
        )
    }
}

@Preview
@Composable
private fun ProfilePictureEditPreview() = ResellPreview(padding = 16.dp) {
    ProfilePictureEdit(imageBitmap = null, onImageTapped = {})
}
