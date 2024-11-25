package com.cornellappdev.resell.android.ui.components.chat.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
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
            .padding(horizontal = 24.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Row {
                ProfilePictureView(
                    imageUrl = imageUrl,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            if (unread) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(width = 2.dp, color = Color.White),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(ResellPurple)
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .absoluteOffset(x = (-200).dp)

                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(end = 16.dp),
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
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(12.dp))
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
                        modifier = Modifier.widthIn(max = 100.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                style = Style.title4,
                text = message,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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

@Preview
@Composable
fun MessageCardPreview() = ResellPreview {
    MessageCard(
        imageUrl = "",
        seller = "Seller",
        title = "Title",
        message = "Message",
        unread = true,
        onClick = {}
    )

    MessageCard(
        imageUrl = "",
        seller = "This seller has a long ass name boy",
        title = "Title",
        message = "Message",
        unread = true,
        onClick = {}
    )

    MessageCard(
        imageUrl = "",
        seller = "This seller has a long ass name boy",
        title = "This item has a long ass title",
        message = "Message",
        unread = true,
        onClick = {}
    )

    MessageCard(
        imageUrl = "",
        seller = "Seller",
        title = "This item has a long ass title",
        message = "Long ass mf message boyyyyy yaaaaa hell yeah",
        unread = true,
        onClick = {}
    )
}
