package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun ForYouComponent(text: String, amount: Int?, images: List<Painter?>, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .size(240.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .clickable(onClick = onClick)
    ) {
        if (images.size >= 4) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.weight(1f)) {
                    ImageBox(images.getOrNull(0), Modifier.weight(1f))
                    ImageBox(images.getOrNull(1), Modifier.weight(1f))
                }
                Row(Modifier.weight(1f)) {
                    ImageBox(images.getOrNull(2), Modifier.weight(1f))
                    ImageBox(images.getOrNull(3), Modifier.weight(1f))
                }
            }
        } else {
            ImageBox(images.getOrNull(0), Modifier.fillMaxSize())
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .aspectRatio(1f)
        ) {
            Text(
                text = text,
                style = Style.title1.copy(color = Color.White),
                modifier = Modifier.align(Alignment.BottomStart)

            )
            if (amount != null) {
                Text(
                    text = "$amount",
                    style = Style.title1.copy(color = Color.White),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }

    }
}


@Composable
fun ImageBox(painter: Painter?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        painter?.let {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
@Preview
private fun ForYouComponentPreview() {
    val placeholder = painterResource(R.drawable.ic_appdev)
    ForYouComponent(
        text = "From Your Purchases",
        images = listOf(placeholder, placeholder, placeholder, placeholder),
        onClick = {},
        amount = null
    )
}
