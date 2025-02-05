package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Warning
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun RequestCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    matchCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onSwipe: () -> Unit = {},
    resetSwipe: UIEvent<Unit>? = null,
) {
    // Define the swipeable state with minimum and maximum bounds
    val swipeableState = rememberSwipeableState(0)

    // Define the size of the delete button when revealed
    val deleteButtonWidth = (-90).dp
    val swipeableThreshold = with(LocalDensity.current) { deleteButtonWidth.toPx() }

    LaunchedEffect(resetSwipe) {
        resetSwipe?.consumeSuspend {
            swipeableState.animateTo(0)
        }
    }

    LaunchedEffect(swipeableState.direction) {
        if (swipeableState.direction == -1f) {
            onSwipe()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultHorizontalPadding()
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    0f to 0,
                    swipeableThreshold to 1
                ),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
            ),
    ) {
        Delete(
            modifier = Modifier.align(Alignment.CenterEnd),
            onDelete = onDelete
        )
        Card(
            modifier = modifier.offset {
                IntOffset(swipeableState.offset.value.roundToInt(), 0)
            },
            matchCount = matchCount,
            title = title,
            description = description,
            onClick = onClick
        )
    }
}

@Preview
@Composable
private fun Delete(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {}
) {
    Surface(
        color = Warning,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.size(78.dp),
        onClick = onDelete
    ) {
        Box {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                tint = Color.White,
                contentDescription = "delete",
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Card(
    modifier: Modifier,
    matchCount: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = Stroke
        ),
        shape = RoundedCornerShape(15.dp),
        onClick = onClick,
        color = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            if (matchCount > 0) {
                Matches(
                    count = matchCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 96.dp)
            ) {
                Text(
                    style = Style.title2,
                    text = title,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    style = Style.body2,
                    text = description
                )
            }
        }
    }
}

@Preview
@Composable
private fun Matches(
    count: Int = 1,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = ResellPurple,
        modifier = modifier
    ) {
        Box(Modifier.padding(horizontal = 6.dp)) {
            Text(
                text = if (count < 10) "$count" else "9+",
                style = Style.subtitle1.copy(fontWeight = FontWeight(500)),
                modifier = Modifier.align(Alignment.TopStart),
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun RequestCardPreview() = ResellPreview {
    var uiEventOne: UIEvent<Unit>? by remember {
        mutableStateOf(null)
    }
    var uiEventTwo: UIEvent<Unit>? by remember {
        mutableStateOf(null)
    }
    RequestCard(
        title = "Standing Desk",
        description = "Adjustable standing desk",
        matchCount = 10,
        onClick = {},
        onDelete = {},
        onSwipe = {
            uiEventTwo = UIEvent(Unit)
        },
        resetSwipe = uiEventOne
    )
    RequestCard(
        title = "StandinJFEHJAKLFHARLHRAJF ARU I need the meaning of life",
        description = "Adjustable standing desk at my fingertips, yes yes yes yes",
        matchCount = 10,
        onClick = {},
        onDelete = {},
        onSwipe = {
            uiEventOne = UIEvent(Unit)
        },
        resetSwipe = uiEventTwo
    )
}
