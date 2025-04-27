package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.ui.theme.Padding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


@Composable
fun ResellListingsScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    paddedTop: Dp = 0.dp,
    emptyState: @Composable () -> Unit = { },
    header: @Composable () -> Unit = {},
    onScrollBottom: () -> Unit = {},
    footer: @Composable () -> Unit = {},
) {
    if (listings.isEmpty()) {
        emptyState()
        return
    }

    // Check if last visible item is the last in the list
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                // -1 to account for the header and footer
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                lastVisibleItemIndex == layoutInfo.totalItemsCount - 1
            }
            .distinctUntilChanged()
            .collect { isAtBottom ->
                if (isAtBottom) {
                    onScrollBottom()
                }
            }
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
        resellListingScroll(listings, onListingPressed, footer)
    }
}

fun calculateItemPadding(addVerticalPadding: Boolean): PaddingValues =
    if (addVerticalPadding) PaddingValues(bottom = 24.dp) else PaddingValues()


fun LazyStaggeredGridScope.resellListingScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    addVerticalPadding: Boolean = false,
    footer: @Composable () -> Unit = {}
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
    item(span = StaggeredGridItemSpan.FullLine) {
        footer()
    }
}