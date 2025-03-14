package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import kotlin.random.Random

@Composable
fun ResellLoadingListingScroll(
    modifier: Modifier = Modifier,
    numCards: Int = Int.MAX_VALUE - 1,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val randomList by remember {
        mutableStateOf(List(50) { i ->
            when (i) {
                0 -> true
                1 -> false
                else -> Random.nextBoolean()
            }
        })
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        state = listState,
        verticalItemSpacing = Padding.medium,
    ) {
        // For consistency with ResellListingScroll
        item(span = StaggeredGridItemSpan.FullLine) {}

        items(numCards) { idx ->
            ResellLoadingCard(
                small = randomList[idx % randomList.size],
                modifier = Modifier.padding(horizontal = Padding.medium / 2f)
            )
        }
    }
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