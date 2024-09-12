package com.cornellappdev.android.resell.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.android.resell.model.Listing
import com.cornellappdev.android.resell.ui.theme.Padding


@Composable
fun ResellListingsScroll(
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    listState: LazyStaggeredGridState,
) {
    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = Padding.medium,
            end = Padding.medium,
            bottom = 100.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(Padding.medium),
        verticalItemSpacing = Padding.medium,
    ) {
        items(items = listings) { item ->
            ResellCard(
                imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                title = "richie",
                price = "$10.00",
                photoHeight = 150.dp + (item.hashCode() % 10) * 8.dp,
            ) {
                onListingPressed(item)
            }
        }
    }
}
