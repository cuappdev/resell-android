package com.cornellappdev.resell.android.ui.components.main

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.nav.NAVBAR_HEIGHT
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.PurpleWash
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.ResellTheme
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel.Category
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel.Condition
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel.ResellFilter

@Composable
fun FilterBottomSheet(
    filter: ResellFilter,
    onFilterChanged: (ResellFilter) -> Unit,
    bottomPadding: Dp,
    lowestPrice: Int = 0,
    highestPrice: Int = 1000,
) {
    val categoriesSelectedCurrent = remember { mutableStateOf(filter.categoriesSelected) }
    val conditionSelectedCurrent = remember { mutableStateOf(filter.conditionSelected) }
    val itemsOnSaleCurrent = remember { mutableStateOf(filter.itemsOnSale) }
    val priceRange = remember { mutableStateOf(filter.priceRange) }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
                itemsOnSale = itemsOnSaleCurrent.value,
                categoriesSelected = categoriesSelectedCurrent.value,
                conditionSelected = conditionSelectedCurrent.value,
                priceRange = priceRange.value,
                lowestPrice = lowestPrice,
                highestPrice = highestPrice,
                onFilterChanged = onFilterChanged,
                onPriceRangeChanged = {
                    priceRange.value = it.start.toInt()..it.endInclusive.toInt()
                },
                onItemsOnSaleChanged = { itemsOnSaleCurrent.value = it },
                toggleCondition = {
                    conditionSelectedCurrent.value =
                        if (it == conditionSelectedCurrent.value) null else it
                },
                toggleCategory = {
                    if (categoriesSelectedCurrent.value.contains(it)) {
                        categoriesSelectedCurrent.value =
                            categoriesSelectedCurrent.value.filter { category -> category != it }
                    } else {
                        categoriesSelectedCurrent.value += it
                    }
                }
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = bottomPadding),
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
private fun AllFilters(
    itemsOnSale: Boolean,
    categoriesSelected: List<Category>,
    conditionSelected: Condition?,
    priceRange: IntRange,
    lowestPrice: Int,
    highestPrice: Int,
    onFilterChanged: (ResellFilter) -> Unit,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onItemsOnSaleChanged: (Boolean) -> Unit,
    toggleCondition: (Condition) -> Unit,
    toggleCategory: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Sort by", style = Style.heading3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Any",
                    color = Secondary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                    tint = Secondary,
                    contentDescription = "Down Arrow",
                    modifier = Modifier.clickable {
                        //todo
                    }
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
        // Using Material 2 RangeSlider to match design
        RangeSlider(
            value = priceRange.first.toFloat()..priceRange.last.toFloat(),
            onValueChange = onPriceRangeChanged,
            valueRange = lowestPrice.toFloat()..highestPrice.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = ResellPurple,
                inactiveTrackColor = ResellPurple.copy(alpha = 0.2f),
                activeTickColor = ResellPurple,
                inactiveTickColor = ResellPurple.copy(alpha = 0.2f)
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("\$${lowestPrice}", style = Style.title4, color = Secondary)
            Text("\$${highestPrice}", style = Style.title4, color = Secondary)
        }
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
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            thickness = 1.dp,
            color = Stroke
        )
        Text(
            text = "Product Category",
            style = Style.heading3,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Category.entries.forEach { item ->
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
        Text(
            text = "Condition",
            style = Style.heading3,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Condition.entries.forEach { condition ->
                ResellChip(
                    selected = conditionSelected == condition,
                    onClick = {
                        toggleCondition(condition)
                    },
                    labelText = condition.label
                )
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Reset", style = Style.title1, modifier = Modifier.clickable {
                Category.entries.forEach { item ->
                    if(categoriesSelected.contains(item)) {
                        toggleCategory(item)
                    }
                }
                Condition.entries.forEach { condition ->
                    if (condition == conditionSelected) {
                        toggleCondition(condition)
                    }
                }
                onItemsOnSaleChanged(false)
                onPriceRangeChanged(lowestPrice.toFloat()..highestPrice.toFloat())
            })
            Button(onClick = {
                onFilterChanged(
                    ResellFilter(
                        priceRange = priceRange,
                        itemsOnSale = itemsOnSale,
                        categoriesSelected = categoriesSelected,
                        conditionSelected = conditionSelected
                    )
                )
            }) {
                Text(text = "Apply Filters", style = Style.title1, color = Color.White)
            }
        }

    }
}

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
    ResellTheme {
        BottomSheetScaffold(
            sheetContent = {
                FilterBottomSheet(
                    ResellFilter(),
                    onFilterChanged = {},
                    bottomPadding = NAVBAR_HEIGHT.dp
                )

            },
            sheetPeekHeight = 750.dp
        ) {
        }
    }
}