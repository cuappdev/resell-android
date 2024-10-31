package com.cornellappdev.resell.android.ui.components.global.messages

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.messages.Notification
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.ResellPurpleWash
import com.cornellappdev.resell.android.ui.theme.Style
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun NotificationCard(
    imageUrl: String,
    title: String,
    timestamp: String,
    unread: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(83.dp)
            .fillMaxWidth()
            .background(if (unread) ResellPurpleWash else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp)),

            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                style = Style.body2,
                text = "richiesun is interested in buying Game",
                color = Color.Black,
            )
            Text(
                style = Style.title4,
                text = timestamp,
                color = Color.Gray
            )

        }
    }
}

@Composable
fun SwipeableNotificationCard(
    notification: Notification,
    imageUrl: String,
    title: String,
    timestamp: String,
    unread: Boolean,
    onArchive: (Notification) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    val swipeOffset = remember { Animatable(0f) }
    val heightAnimatable = remember { Animatable(83f) }
    val maxSwipeOffset = 250.dp.value // Maximum swipe distance for revealing the archive option
    val minSwipeVelocity = 1000f
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ResellPurple)
            .height(heightAnimatable.value.dp) // Keep consistent with NotificationCard height
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (swipeOffset.value > maxSwipeOffset / 2 || swipeOffset.velocity > minSwipeVelocity) {
                                swipeOffset.animateTo(1500f)
                                heightAnimatable.animateTo(0f)
                                onArchive(notification)
                                swipeOffset.snapTo(1500f)
                                heightAnimatable.snapTo(0f)

                            } else {
                                swipeOffset.animateTo(0f) // Animate back if not far enough
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        coroutineScope.launch{
                            val newOffset = (swipeOffset.value + dragAmount).coerceIn(0f, maxSwipeOffset)
                            swipeOffset.snapTo(newOffset)
                        }

                    }
                )
            }
    ) {
        // Notification Card Content
        NotificationCard(
            imageUrl = imageUrl,
            title = title,
            timestamp = timestamp,
            unread = unread,
            onClick = onClick,
            modifier = Modifier
                .offset { IntOffset(swipeOffset.value.roundToInt(), 0) } // Swipe offset applied here
        )
    }
}

