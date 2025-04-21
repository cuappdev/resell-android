package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import kotlin.random.Random

@Composable
fun ResellLoadingListingScroll(
    modifier: Modifier = Modifier,
    // -1 to avoid overflow in the grid
    numCards: Int = Int.MAX_VALUE - 1,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    header: @Composable () -> Unit = {},
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        state = listState,
        verticalItemSpacing = 24.dp
    ) {
        item(span = StaggeredGridItemSpan.FullLine) { header() }
        resellLoadingListingScroll(numCards = numCards)

    }
}

fun LazyStaggeredGridScope.resellLoadingListingScroll(numCards: Int) {
    val booleans = getRandomList()
    // LazyVerticalStaggeredGrid takes at most Int.MAX_VALUE items
    items(numCards.coerceIn(0, Int.MAX_VALUE - 1)) { idx ->
        val padding = calculateItemPadding(idx)
        ResellLoadingCard(
            small = booleans[idx % booleans.size],
            modifier = Modifier.padding(padding)
        )
    }
}

private var randomList = emptyList<Boolean>()
private fun getRandomList(): List<Boolean> {
    if (randomList.isEmpty()) {
        randomList = List(50) { i ->
            when (i) {
                0 -> true
                1 -> false
                else -> Random.nextBoolean()
            }
        }
    }
    return randomList
}

@Preview
@Composable
private fun PreviewHomeLoadingScroll() = ResellPreview {
    ResellLoadingListingScroll()
}


@Preview
@Composable
private fun PreviewSearchLoadingScroll() = ResellPreview {
    ResellLoadingListingScroll(numCards = 2)
}