package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style

private val DividerHeight = 1.dp
private val IndicatorHeight = 2.dp
private const val INDICATOR_ANIMATION_MILLIS = 200

data class LabeledTab(
    val count: Int,
    val iconRes: Int,
    val contentDescription: String,
    val iconSize: Dp = 24.dp,
    val title: String = "",
)

@Composable
fun ResellLabeledTabBar(
    tabs: List<LabeledTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(start = 48.dp, end = 48.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { i, tab ->
                LabeledTabItem(
                    tab = tab,
                    selected = i == selectedIndex,
                    onClick = { onTabSelected(i) }
                )
            }
        }

        val indicatorPosition by animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
            animationSpec = tween(durationMillis = INDICATOR_ANIMATION_MILLIS),
            label = "tab indicator"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IndicatorHeight)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(DividerHeight)
                    .background(Stroke)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f / tabs.size)
                    .fillMaxHeight()
                    .graphicsLayer { translationX = indicatorPosition * size.width }
                    .background(Primary)
            )
        }
    }
}

@Composable
private fun LabeledTabItem(
    tab: LabeledTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color by animateColorAsState(
        targetValue = if (selected) Primary else IconInactive,
        label = "tab color"
    )

    Row(
        modifier = Modifier
            .selectable(
                selected = selected,
                interactionSource = null,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { contentDescription = tab.contentDescription }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = tab.iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(tab.iconSize)
        )
        Text(
            text = buildAnnotatedString {
                if (tab.title.isNotEmpty()) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) { append(tab.title) }
                    append(" ")
                }
                append("(${tab.count})")
            },
            style = Style.body2,
            color = color,
            modifier = Modifier.clearAndSetSemantics {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LabeledTabBarPreview() = ResellPreview {
    var selected by remember { mutableIntStateOf(0) }

    ResellLabeledTabBar(
        tabs = listOf(
            LabeledTab(
                count = 12,
                iconRes = R.drawable.ic_shop,
                contentDescription = "Listings, 12",
            ),
            LabeledTab(
                count = 8,
                iconRes = R.drawable.ic_selected_star,
                contentDescription = "Reviews, 4.6 average rating, 8 reviews",
                iconSize = 20.dp,
                title = "4.6",
            ),
        ),
        selectedIndex = selected,
        onTabSelected = { selected = it }
    )
}
