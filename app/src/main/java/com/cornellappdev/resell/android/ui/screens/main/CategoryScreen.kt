package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.main.FilterBottomSheet
import com.cornellappdev.resell.android.ui.components.main.ResellSearchBar
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style.heading3
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    // category: ResellFilter.FilterCategory,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    onExit: () -> Unit
) {
    val uiState = categoryViewModel.collectUiStateValue()
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // Initially apply the category to the filter
//    if (uiState.filter.categoriesSelected != listOf(category)) {
//        categoryViewModel.onFilterChanged(uiState.filter.copy(categoriesSelected = listOf(category)))
//    }

    CategoryScreenContent(
        loadedState = uiState.loadedState,
        category = uiState.filter.categoriesSelected.firstOrNull() ?: ResellFilter.FilterCategory.OTHER,
        onSearchPressed = categoryViewModel::onSearchPressed,
        onFilter = {
            coroutineScope.launch {
                sheetState.show()
            }
        },
        sheetState = sheetState,
        filter = uiState.filter,
        onFilterChanged = {
            categoryViewModel.onFilterChanged(it)
            coroutineScope.launch {
                sheetState.hide()
            }
        },
        listings = uiState.listings,
        onListingPressed = categoryViewModel::onListingPressed,
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
            }
        },
        onExit = onExit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreenContent(
    loadedState: ResellApiState,
    category: ResellFilter.FilterCategory,
    onSearchPressed: () -> Unit,
    onFilter: () -> Unit,
    sheetState: SheetState,
    filter: ResellFilter,
    onFilterChanged: (ResellFilter) -> Unit,
    listings: List<Listing>,
    onListingPressed: (Listing) -> Unit,
    onDismissRequest: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Header(category, onExit)
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .defaultHorizontalPadding()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResellSearchBar(onClick = onSearchPressed, modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Filter",
                modifier = Modifier.clickable(onClick = onFilter)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        when (loadedState) {
            ResellApiState.Loading -> ResellLoadingListingScroll(modifier = Modifier.defaultHorizontalPadding())
            ResellApiState.Success -> ResellListingsScroll(
                listings = listings,
                onListingPressed = onListingPressed,
                modifier = Modifier
                    .defaultHorizontalPadding()
            )

            ResellApiState.Error -> {}
        }
        if (sheetState.isVisible) {
            ModalBottomSheet(onDismissRequest = onDismissRequest) {
                FilterBottomSheet(
                    filter = filter,
                    onFilterChanged = onFilterChanged,
                    includeCategory = false
                )
            }
        }
    }
}

@Composable
private fun Header(category: ResellFilter.FilterCategory, onExit: () -> Unit) {
    Box(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = category.label,
                style = heading3
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back",
            modifier = Modifier.clickable { onExit() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CategoryScreenPreview() = ResellPreview {
    CategoryScreenContent(
        category = ResellFilter.FilterCategory.CLOTHING,
        onSearchPressed = {},
        onFilter = {},
        sheetState = rememberModalBottomSheetState(),
        filter = ResellFilter(categoriesSelected = listOf(ResellFilter.FilterCategory.CLOTHING)),
        onFilterChanged = {},
        listings = List(5) { dumbListing },
        onListingPressed = {},
        loadedState = ResellApiState.Loading,
        onDismissRequest = {},
        onExit = {}
    )
}