package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    itemsOnSale: Boolean,
    onItemsOnSaleChanged: (Boolean) -> Unit,
    categoriesToggled: List<Boolean>,
    onCategoryToggled: (Int) -> Unit = {},
    conditionsToggled: List<Boolean>,
    onConditionToggled: (Int) -> Unit,
    reset: () -> Unit,
    applyFilters: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 36.dp),
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
                    text = "Any",
                    color = Secondary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )
                var sliderPosition by remember { mutableStateOf(0f..100f) }
                RangeSlider(
                    value = sliderPosition,
                    onValueChange = { range -> sliderPosition = range },
                    valueRange = 0f..100f,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("\$0", style = Style.title4)
                    Text("\$10000", style = Style.title4)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Items on Sale", fontWeight = FontWeight.Normal, fontSize = 20.sp)
                    Switch(
                        checked = itemsOnSale,
                        onCheckedChange = onItemsOnSaleChanged,
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
                    val categories = listOf(
                        "Clothing",
                        "Books",
                        "School",
                        "Electronics",
                        "Handmade",
                        "Sports & Outdoors",
                        "Other"
                    )
                    categories.forEachIndexed { idx, label ->
                        FilterChip(
                            selected = categoriesToggled[idx],
                            onClick = { onCategoryToggled(idx) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            },
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(width = 1.dp, color = Stroke),
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
                    val categories = listOf(
                        "Gently Used",
                        "Used",
                        "Never Used"
                    )
                    categories.forEachIndexed { idx, label ->
                        FilterChip(
                            selected = conditionsToggled[idx],
                            onClick = { onConditionToggled(idx) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            },
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(width = 1.dp, color = Stroke),
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
                        reset()
                    })
                    Button(onClick = {
                        applyFilters()
                    }) {
                        Text(text = "Apply Filters", style = Style.title1, color = Color.White)
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewFilterBottomSheet() {
    BottomSheetScaffold(
        sheetContent = {
            FilterBottomSheet(
                itemsOnSale = false,
                onItemsOnSaleChanged = {},
                conditionsToggled = List(7) { false },
                onConditionToggled = {},
                categoriesToggled = List(7) { false },
                reset = {},
                applyFilters = {})
        },
        sheetPeekHeight = 750.dp
    ) {
    }
}