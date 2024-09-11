package com.cornellappdev.android.resell.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.android.resell.R
import com.cornellappdev.android.resell.ui.components.global.ListingCard
import com.cornellappdev.android.resell.ui.components.global.Tag
import com.cornellappdev.android.resell.ui.theme.Padding
import com.cornellappdev.android.resell.ui.theme.Primary
import com.cornellappdev.android.resell.ui.theme.Style
import com.cornellappdev.android.resell.ui.util.defaultHorizontalPadding

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        HomeHeader()
        HomeListingsScroll()
    }
}

@Preview
@Composable
private fun HomeHeader() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultHorizontalPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "resell",
                style = Style.resellBrand
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search",
                tint = Primary,
                modifier = Modifier.size(25.dp)
            )
        }

        // filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Padding.medium, Alignment.Start),
        ) {
            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }

            items(items = List(10) { it }) { item ->
                Tag(
                    text = "filter $item",
                    active = item % 5 == 0,
                    onClick = {}
                )

            }
        }

        Spacer(modifier = Modifier.height(Padding.medium))
    }
}

@Composable
private fun HomeListingsScroll() {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = Padding.medium,
            end = Padding.medium,
            bottom = 100.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(Padding.medium),
        verticalItemSpacing = Padding.medium,
    ) {
        items(items = List(20) { it }) { item ->
            ListingCard(
                imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                title = "richie",
                price = "$10.00",
                photoHeight = 150.dp + item * 8.dp,
            ) {

            }
        }
    }
}
