package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview

@Composable
fun ResellLoadingListingScroll(
    modifier: Modifier = Modifier,
    numCards: Int = 5,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalItemSpacing = 24.dp,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (numCards >= 1) {
            item {
                ResellLoadingCard(small = true)
            }
            items(numCards - 1) {
                ResellLoadingCard(small = false)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeLoadingScroll() = ResellPreview {
    ResellLoadingListingScroll(numCards = 5)
}


@Preview
@Composable
private fun PreviewSearchLoadingScroll() = ResellPreview {
    ResellLoadingListingScroll(numCards = 2)
}