package com.cornellappdev.resell.android.ui.components.global

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
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
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    modifier: Modifier = Modifier,
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
        verticalItemSpacing = Padding.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            header()
        }

        items(items = listings) { item ->
            ResellCard(
                imageUrl = item.image,
                title = item.title,
                price = item.price,
                modifier = Modifier.padding(horizontal = Padding.medium / 2f)
            ) {
                onListingPressed(item)
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            footer()
        }
    }
}
