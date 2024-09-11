package com.cornellappdev.android.resell.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.cornellappdev.android.resell.R
import com.cornellappdev.android.resell.ui.theme.Padding
import com.cornellappdev.android.resell.ui.theme.Stroke
import com.cornellappdev.android.resell.ui.theme.Style

/**
 * @param price The price of the listing, as $###.##.
 */
@Composable
fun ListingCard(
    imageUrl: String,
    title: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // TODO: Find out how component height is determined.
    Column(modifier = modifier
        .widthIn(max = 0.5f * screenWidth)
        .fillMaxWidth()
        .clip(RoundedCornerShape(size = 8.dp))
        .background(Color.White)
        .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_background)
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
        ListingCard(
            imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
            title = "Title",
            price = "$10.00",
            onClick = {}
        )

        Spacer(modifier = Modifier.padding(Padding.large))

        ListingCard(
            imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
            title = "Richie man",
            price = "$999.99",
            onClick = {}
        )
    }
}
