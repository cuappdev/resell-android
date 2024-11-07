package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ripple
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.ResellPreview

/**
 * A tab bar with a list of images that can be selected. Assumes icons are 24.dp.
 */
@Composable
fun ResellTabBar(
    modifier: Modifier = Modifier,
    painterIds: List<Int>,
    selectedPainter: Int,
    onTabSelected: (Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            painterIds.forEachIndexed { i, painter ->
                Tab(
                    painter = painter,
                    selected = selectedPainter == i,
                    onTabSelected = { onTabSelected(i) }
                )
            }
        }

        BarSlider(size = painterIds.size, selectedPainter = selectedPainter)
    }
}

@Composable
private fun BarSlider(
    size: Int,
    selectedPainter: Int
) {
    // Screen width
    val barWidth = (LocalConfiguration.current.screenWidthDp.toFloat() / size).dp

    val numToRight = size - selectedPainter - 1

    val leftWeight = animateFloatAsState(
        targetValue = selectedPainter.toFloat().coerceAtLeast(minimumValue = 0.0001f),
        label = "left bar"
    )

    val rightWeight = animateFloatAsState(
        targetValue = numToRight.toFloat().coerceAtLeast(minimumValue = 0.0001f),
        label = "right bar"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(IconInactive)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(
                modifier = Modifier.weight(
                    leftWeight.value
                )
            )

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(1.dp)
                    .background(Primary)
            )

            Spacer(
                modifier = Modifier.weight(
                    rightWeight.value
                )
            )
        }
    }
}

@Composable
private fun RowScope.Tab(
    modifier: Modifier = Modifier,
    painter: Int,
    selected: Boolean,
    onTabSelected: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (selected) Primary else IconInactive,
        label = "tab color"
    )

    Box(modifier = modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple()
        ) {
            onTabSelected()
        }
        .weight(1f)
    ) {
        Icon(
            painter = painterResource(id = painter),
            contentDescription = "tab",
            tint = color,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp)
                .size(24.dp)
        )
    }
}

@Preview
@Composable
private fun TabBarPreview() = ResellPreview {
    var selected by remember { mutableIntStateOf(0) }

    ResellTabBar(
        painterIds = listOf(R.drawable.ic_search, R.drawable.ic_home, R.drawable.ic_user),
        selectedPainter = selected,
        onTabSelected = {
            selected = it
        }
    )

    Spacer(Modifier.height(16.dp))
}
