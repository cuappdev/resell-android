package com.cornellappdev.resell.android.ui.components.global.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun MessageCard(
    imageUrl: String,
    seller: String,
    title: String,
    message: String,
    unread: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (unread) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ResellPurple)
                    .height(10.dp)
                    .width(10.dp)
            )
            Spacer(modifier.width(12.dp))
        } else {
            Spacer(modifier.width(22.dp))
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp)),

            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier.height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = Style.title1,
                    text = seller,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.padding(Padding.extraSmall))
                Row(
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(25.dp))
                        .fillMaxHeight()
                        .padding(horizontal = Padding.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = Style.title4,
                        text = title,
                        color = Color.Gray,
                    )
                }
            }
            Text(
                style = Style.title4,
                text = message,
                color = Color.Gray,
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "chat",
            tint = Color.Gray,
            modifier = Modifier
                .size(12.dp)
                .scale(-1f)
        )
    }
}


