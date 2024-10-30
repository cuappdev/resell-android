package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style

/**
 * @param price The price of the listing, as $###.##.
 */
@Composable
fun ResellCard(
    imageUrl: String,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    photoHeight: Dp = 220.dp,
    onClick: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // TODO: Find out how component height is determined.
    Column(
        modifier = modifier
            .widthIn(max = 0.5f * screenWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(photoHeight)
                .heightIn(max = 220.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop
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

@Preview
@Composable
private fun PreviewListingCard() {
    Column(modifier = Modifier.fillMaxSize()) {
        ResellCard(
            imageUrl = "",
            title = "Title",
            price = "$10.00",
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(Padding.large))

        ResellCard(
            imageUrl = "",
            title = "Richie man",
            price = "$999.99",
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(Padding.large))

        ResellCard(
            imageUrl = "",
            title = "Richie man with a damn long listing name",
            price = "$999.99",
            onClick = {}
        )
    }
}
