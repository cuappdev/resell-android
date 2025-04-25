package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.Listing


@Composable
fun ResellListingsScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    paddedTop: Dp = 0.dp,
    emptyState: @Composable () -> Unit = { },
    header: @Composable () -> Unit = {},
) {
    if (listings.isEmpty()) {
        emptyState()
        return
    }

    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            bottom = 100.dp,
            top = paddedTop,
        ),
        verticalItemSpacing = 24.dp,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            header()
        }
        resellListingScroll(listings, onListingPressed)
    }
}

fun calculateItemPadding(addVerticalPadding: Boolean): PaddingValues =
    if (addVerticalPadding) PaddingValues(bottom = 24.dp) else PaddingValues()


fun LazyStaggeredGridScope.resellListingScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    addVerticalPadding: Boolean = false
) {
    itemsIndexed(items = listings) { _, item ->
        val padding = calculateItemPadding(addVerticalPadding)
        ResellCard(
            imageUrl = item.image,
            title = item.title,
            price = item.price,
            modifier = Modifier.padding(padding)
        ) {
            onListingPressed(item)
        }
    }
}