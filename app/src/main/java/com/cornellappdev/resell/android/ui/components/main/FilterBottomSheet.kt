package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.ui.components.global.Dropdown
import com.cornellappdev.resell.android.ui.components.global.ResellSlider
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.PurpleWash
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import kotlinx.coroutines.launch

@Composable
fun FilterBottomSheet(
    filter: ResellFilter,
    onFilterChanged: (ResellFilter) -> Unit,
    lowestPrice: Int = 0,
    highestPrice: Int = 1000,
    includeCategory: Boolean = true
) {
    var categoriesSelectedCurrent by remember { mutableStateOf(filter.categoriesSelected) }
    var conditionSelectedCurrent by remember { mutableStateOf(filter.conditionSelected) }
    var itemsOnSaleCurrent by remember { mutableStateOf(filter.itemsOnSale) }
    var priceRange by remember { mutableStateOf(filter.priceRange) }
    var sortBy by remember { mutableStateOf(ResellFilter.SortBy.ANY) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState
    ) {
        item {
            Text(
                text = "Filters",
                style = Style.heading2,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
        item {
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Stroke)
        }
        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
        item {
            AllFilters(
                originalFilter = filter,
                sortBy = sortBy,
                itemsOnSale = itemsOnSaleCurrent,
                includeCategory = includeCategory,
                categoriesSelected = categoriesSelectedCurrent,
                conditionSelected = conditionSelectedCurrent,
                priceRange = priceRange,
                lowestPrice = lowestPrice,
                highestPrice = highestPrice,
                onFilterChanged = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                    onFilterChanged(it)
                },
                onPriceRangeChanged = {
                    priceRange = it.start.toInt()..it.endInclusive.toInt()
                },
                onItemsOnSaleChanged = { itemsOnSaleCurrent = it },
                toggleCondition = {
                    conditionSelectedCurrent =
                        if (it == conditionSelectedCurrent) null else it
                },
                toggleCategory = {
                    if (categoriesSelectedCurrent.contains(it)) {
                        categoriesSelectedCurrent =
                            categoriesSelectedCurrent.filter { category -> category != it }
                    } else {
                        categoriesSelectedCurrent += it
                    }
                },
                onSelectSortBy = {
                    sortBy = it
                }
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = 36.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AllFilters(
    originalFilter: ResellFilter,
    sortBy: ResellFilter.SortBy,
    itemsOnSale: Boolean,
    includeCategory: Boolean,
    categoriesSelected: List<ResellFilter.FilterCategory>,
    conditionSelected: ResellFilter.FilterCondition?,
    priceRange: IntRange,
    lowestPrice: Int,
    highestPrice: Int,
    onFilterChanged: (ResellFilter) -> Unit,
    onSelectSortBy: (ResellFilter.SortBy) -> Unit,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onItemsOnSaleChanged: (Boolean) -> Unit,
    toggleCondition: (ResellFilter.FilterCondition) -> Unit,
    toggleCategory: (ResellFilter.FilterCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        SortBy(sortBy = sortBy, onSelectSortBy = onSelectSortBy)
        PriceRangeFilter(
            priceRange = priceRange,
            lowestPrice = lowestPrice,
            highestPrice = highestPrice,
            onPriceRangeChanged = onPriceRangeChanged
        )

        if (includeCategory) {
            CategoryFilters(categoriesSelected, toggleCategory)
        }
        Condition(conditionSelected, toggleCondition)

        Spacer(modifier = Modifier.height(36.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reset",
                style = Style.title1,
                color = ResellPurple,
                modifier = Modifier.clickable {
                    ResellFilter.FilterCategory.entries.forEach { item ->
                        if (categoriesSelected.contains(item)) {
                            toggleCategory(item)
                        }
                    }
                    ResellFilter.FilterCondition.entries.forEach { condition ->
                        if (condition == conditionSelected) {
                            toggleCondition(condition)
                        }
                    }
                    onItemsOnSaleChanged(false)
                    onPriceRangeChanged(lowestPrice.toFloat()..highestPrice.toFloat())
                })
            Button(
                onClick = {
                    onFilterChanged(
                        ResellFilter(
                            priceRange = priceRange,
                            itemsOnSale = itemsOnSale,
                            categoriesSelected = categoriesSelected,
                            conditionSelected = conditionSelected,
                            sortBy = sortBy
                        )
                    )
                }, colors = ButtonColors(
                    containerColor = ResellPurple,
                    contentColor = Color.White,
                    disabledContainerColor = ResellPurple.copy(alpha = 0.4f),
                    disabledContentColor = Color.White
                ),
                enabled = checkIfFilterChanged(
                    sortBy,
                    priceRange,
                    categoriesSelected,
                    conditionSelected,
                    originalFilter
                )
            ) {
                Text(text = "Apply Filters", style = Style.title1, color = Color.White)
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SortBy(sortBy: ResellFilter.SortBy, onSelectSortBy: (ResellFilter.SortBy) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Sort by", style = Style.heading3)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sortBy.label,
                color = Secondary,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            )
            Dropdown(onSelectSortBy = onSelectSortBy)
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        thickness = 1.dp,
        color = Stroke
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PriceRangeFilter(
    priceRange: IntRange,
    lowestPrice: Int,
    highestPrice: Int,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Text(
        text = "Price Range",
        style = Style.heading3,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = rangeString(priceRange, lowestPrice, highestPrice),
        color = Secondary,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )
    ResellSlider(
        range = priceRange,
        lowestValue = lowestPrice,
        highestValue = highestPrice,
        onRangeChanged = onPriceRangeChanged
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$$lowestPrice", style = Style.title4, color = Secondary)
        Text("$$highestPrice", style = Style.title4, color = Secondary)
    }
    // TODO - uncomment once backend handles items on sale
//        ItemsOnSale(itemsOnSale, onItemsOnSaleChanged)
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        thickness = 1.dp,
        color = Stroke
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Condition(
    conditionSelected: ResellFilter.FilterCondition?,
    toggleCondition: (ResellFilter.FilterCondition) -> Unit
) {
    Text(
        text = "Condition",
        style = Style.heading3,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ResellFilter.FilterCondition.entries.forEach { condition ->
            ResellChip(
                selected = conditionSelected == condition,
                onClick = {
                    toggleCondition(condition)
                },
                labelText = condition.label
            )
        }
    }
}


@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CategoryFilters(
    categoriesSelected: List<ResellFilter.FilterCategory>,
    toggleCategory: (ResellFilter.FilterCategory) -> Unit
) {
    Text(
        text = "Product Category",
        style = Style.heading3,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ResellFilter.FilterCategory.entries.forEach { item ->
            ResellChip(
                selected = categoriesSelected.contains(item),
                onClick = { toggleCategory(item) },
                labelText = item.label
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        thickness = 1.dp,
        color = Stroke
    )
}

private fun checkIfFilterChanged(
    sortBy: ResellFilter.SortBy,
    priceRange: IntRange,
    categoriesSelected: List<ResellFilter.FilterCategory>,
    conditionSelected: ResellFilter.FilterCondition?,
    originalFilter: ResellFilter
): Boolean {
    return sortBy != originalFilter.sortBy ||
            priceRange != originalFilter.priceRange ||
            categoriesSelected != originalFilter.categoriesSelected ||
            conditionSelected != originalFilter.conditionSelected
}

@Composable
private fun ItemsOnSale(
    itemsOnSale: Boolean,
    onItemsOnSaleChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Items on Sale",
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = Secondary
        )
        Switch(
            checked = itemsOnSale,
            onCheckedChange = onItemsOnSaleChanged,
            colors = SwitchColors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ResellPurple,
                checkedBorderColor = ResellPurple,
                checkedIconColor = Color.White,

                uncheckedThumbColor = IconInactive,
                uncheckedTrackColor = Color.White,
                uncheckedBorderColor = IconInactive,
                uncheckedIconColor = IconInactive,

                disabledCheckedThumbColor = Color.Gray,
                disabledCheckedTrackColor = Color.Gray,
                disabledCheckedBorderColor = Color.Gray,
                disabledCheckedIconColor = Color.Gray,
                disabledUncheckedThumbColor = Color.Gray,
                disabledUncheckedTrackColor = Color.Gray,
                disabledUncheckedBorderColor = Color.Gray,
                disabledUncheckedIconColor = Color.Gray
            ),
            modifier = Modifier.scale(0.8f)
        )
    }
}

/**
 * Returns a formatted price range string: "Any", "Up to $X", "$X +", or "$X to $Y".
 */
private fun rangeString(priceRange: IntRange, lowestPrice: Int, highestPrice: Int): String {
    val lowerBound = priceRange.first
    val upperBound = priceRange.last
    if (lowerBound == lowestPrice && upperBound == highestPrice) {
        return "Any"
    }
    if (lowerBound == lowestPrice) return "Up to $$upperBound"
    if (upperBound == highestPrice) return "$$lowerBound +"
    return "$$lowerBound to $$upperBound"
}

@Composable
private fun ResellChip(selected: Boolean, onClick: () -> Unit, labelText: String) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = labelText,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            )
        },
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) ResellPurple else Stroke
        ),
        colors = if (!selected) {
            FilterChipDefaults.filterChipColors()
        } else {
            FilterChipDefaults.filterChipColors(
                labelColor = ResellPurple,
                disabledContainerColor = Color.Unspecified,
                disabledLabelColor = Color.Black,
                selectedContainerColor = PurpleWash,
                selectedLabelColor = ResellPurple,
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFilterBottomSheet() {
    // Use interactive mode to see the bottom sheet in both its expanded and half-expanded states.
    ResellPreview {
        val sheetState = rememberModalBottomSheetState()
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            sheetState = sheetState
        ) {
            FilterBottomSheet(
                ResellFilter(),
                onFilterChanged = {}
            )
        }
    }
}