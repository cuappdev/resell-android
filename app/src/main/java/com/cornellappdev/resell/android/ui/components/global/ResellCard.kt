package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.shimmer
import com.cornellappdev.resell.android.util.windowWidth

/**
 * @param image What to show in the image slot.
 * @param price The price of the listing, as $###.##.
 */
@Composable
fun ResellCard(
    image: ImageState,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val screenWidth = windowWidth()

    Column(
        modifier = modifier
            .widthIn(max = 0.5f * screenWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AnimatedClampedAsyncImage(
            image = image
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = Style.title3,
                text = title,
                color = Color.Black,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
            Text(
                style = Style.title4,
                text = price
            )
        }
    }
}

@Composable
fun AnimatedClampedAsyncImage(
    image: ImageState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.heightIn(min = 130.dp, max = 220.dp)) {
        AnimatedContent(targetState = image, label = "image loading") { response ->
            when (response) {
                ImageState.Loading -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .shimmer()
                            .fillMaxWidth()
                    )
                }

                ImageState.Error -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .background(
                                Secondary
                            )
                            .fillMaxWidth()
                    )
                }

                is ImageState.Loaded -> {
                    Image(
                        bitmap = response.bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResellCardPreview() = ResellPreview(
    backgroundColor = Color.Transparent
) {
    ResellCard(
        image = ImageState.Loading,
        title = "Title",
        price = "$10.00",
        onClick = {},
    )

    Spacer(modifier = Modifier.padding(Padding.large))

    ResellCard(
        image = ImageState.Error,
        title = "Richie man",
        price = "$999.99",
        onClick = {},
    )

    Spacer(modifier = Modifier.padding(Padding.large))

    ResellCard(
        image = ImageState.Loaded(ImageBitmap(1, 1)),
        title = "Richie man with a damn long listing name",
        price = "$999.99",
        onClick = {},
    )
}
