package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Padding

@Composable
fun ResellLoadingListingsScroll(
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    modifier: Modifier = Modifier,
    paddedTop: Dp = 0.dp,
    header: @Composable () -> Unit = {},
) {
    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            bottom = 100.dp,
            top = paddedTop,
        ),
        verticalItemSpacing = Padding.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            header()
        }
        val cardModifier = Modifier.padding(horizontal = Padding.medium / 2f)
        item {
            ResellLoadingCard(cardModifier, small = true)
        }
        items(4) {
            ResellLoadingCard(cardModifier, small = false)
        }
    }
}

@Preview
@Composable
fun PreviewResellLoadingListingsScroll() {
    ResellLoadingListingsScroll()
}