package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.resell.android.model.Listing
import com.cornellappdev.resell.android.ui.theme.Padding


@Composable
fun ResellListingsScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    listState: LazyStaggeredGridState,
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

        items(items = listings) { item ->
            ResellCard(
                imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                title = "richie",
                price = "$10.00",
                photoHeight = 150.dp + (item.hashCode() % 10) * 8.dp,
                modifier = Modifier.padding(horizontal = Padding.medium / 2f)
            ) {
                onListingPressed(item)
            }
        }
    }
}
