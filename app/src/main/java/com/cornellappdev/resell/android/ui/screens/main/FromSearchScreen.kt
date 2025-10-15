package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellCard
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.FromSearchesViewModel
import kotlinx.coroutines.launch

@Composable
fun FromSearchScreen(
    modifier: Modifier = Modifier,
    fromSearchViewModel: FromSearchesViewModel = hiltViewModel(),
) {
    val fromSearchUiState = fromSearchViewModel.collectUiStateValue()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()
    val categories = fromSearchUiState.listings

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxSize()
    ) {
        Header(
            onBack = { fromSearchViewModel.onBackPressed() },
            onTopPressed = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            })

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp, top = 12.dp),
            verticalItemSpacing = 24.dp,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = category.first, fontSize = 18.sp)
                            Text(
                                text = "Hide",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { }
                            )
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(category.second) { item ->
                                ResellCard(
                                    imageUrl = item.image,
                                    title = item.title,
                                    price = item.price,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    fromSearchViewModel.onListingPressed(item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit, onTopPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onTopPressed()
            }
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "back",
            modifier = Modifier
                .clickable(
                    onClick = onBack
                )
                .align(Alignment.CenterStart)
        )
        Text(
            text = "From Your Searches",
            style = Style.heading3,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun HeaderPreview() {
    Header({}, {})
}
