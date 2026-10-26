package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.Listing
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
    bottomLoading: Boolean = false,
) {
    if (listings.isEmpty()) {
        emptyState()
        return
    }

    // Check if the last real listing item is visible
    val currentListings by rememberUpdatedState(listings)
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val lastId = currentListings.lastOrNull()?.id
                lastId to layoutInfo.isItemWithKeyVisible(lastId)
            }
            .distinctUntilChanged()
            .collect { (_, isAtBottom) ->
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
        resellListingScroll(listings, onListingPressed, footer, bottomLoading = bottomLoading)
    }
}

fun calculateItemPadding(addVerticalPadding: Boolean): PaddingValues =
    if (addVerticalPadding) PaddingValues(bottom = 24.dp) else PaddingValues()

fun LazyStaggeredGridLayoutInfo.isItemWithKeyVisible(key: Any?): Boolean =
    key != null && visibleItemsInfo.any { it.key == key }

fun LazyStaggeredGridScope.resellListingScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    footer: @Composable () -> Unit = {},
    addVerticalPadding: Boolean = false,
    bottomLoading: Boolean = false,
) {
    itemsIndexed(items = listings.distinctBy { it.id }, key = { _, item -> item.id }) { _, item ->
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
    if (bottomLoading) {
        resellLoadingListingScroll(numCards = 2, addVerticalPadding = addVerticalPadding)
    }
    item(span = StaggeredGridItemSpan.FullLine) {
        footer()
    }
}