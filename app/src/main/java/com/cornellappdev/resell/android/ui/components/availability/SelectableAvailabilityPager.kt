package com.cornellappdev.resell.android.ui.components.availability

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.components.availability.helper.AvailabilityPagerContainer
import com.cornellappdev.resell.android.ui.components.availability.helper.SelectableAvailabilityGrid
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun SelectableAvailabilityPager(
    title: String,
    subtitle: String,
    initialSelectedAvailabilities: List<LocalDateTime> = emptyList(),
    scrollRange: Pair<Int, Int> = 0 to 6,
    modifier: Modifier = Modifier,
    setSelectedAvailabilities: (List<LocalDateTime>) -> Unit,
) {
    /** Guaranteed to have non-null mappings for any page in the scroll range. */
    var selectedDatesByPage: Map<Int, List<LocalDateTime>> by remember {
        mutableStateOf(buildMap {
            for (i in scrollRange.first..scrollRange.second) {
                put(i, emptyList())
            }
        })
    }

    // Initialize the selected dates by page.
    LaunchedEffect(initialSelectedAvailabilities) {
        // Add each availability to the correct page based on its date.
        //  Each page corresponds to an increment of 3 days, and each
        //  inner list corresponds the availabilities for that 3-day period.
        //  Thus, we must add to the correct 3 day period.
        initialSelectedAvailabilities.forEach { availability ->
            val today = LocalDate.now()
            val dayDifference = today.until(availability.toLocalDate()).days
            val pageIndex = Math.floorDiv(dayDifference, 3)

            Log.d(
                "helpme",
                "day difference: $dayDifference for $availability, resuling in page index $pageIndex"
            )

            if (selectedDatesByPage[pageIndex] != null) {
                val list = selectedDatesByPage[pageIndex]!!.toMutableList()
                list.add(availability)
                val copy = selectedDatesByPage.toMutableMap()
                copy[pageIndex] = list
                selectedDatesByPage = copy.toMap()

                Log.d("helpme", "non-null hit, new list: ${selectedDatesByPage[pageIndex]}")
            } else {
                Log.d("helpme", "null hit")
            }

            Log.d("helpme", "calculating... $availability")
        }
    }

    AvailabilityPagerContainer(
        startDate = LocalDate.now(),
        scrollRange = scrollRange,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
    ) { dates, page ->
        SelectableAvailabilityGrid(
            dates = dates,
            selectedAvailabilities = selectedDatesByPage[page]!!,
            setSelectedAvailabilities = { availabilities ->
                val updatedDates = selectedDatesByPage.mapValues { (index, localDateTimes) ->
                    if (index != page) {
                        localDateTimes
                    } else {
                        availabilities
                    }
                }
                selectedDatesByPage = updatedDates
                setSelectedAvailabilities(updatedDates.values.flatten())
            })
    }
}

@Preview
@Composable
private fun SelectableAvailabilityPagerPreview() = ResellPreview {
    var selectedAvailabilities by remember { mutableStateOf(emptyList<LocalDateTime>()) }
    Column {
        Text("Selected availabilities = $selectedAvailabilities")
        SelectableAvailabilityPager(
            title = "When are you free?",
            subtitle = "Fill it out or else.",
        ) { selectedAvailabilities = it }
    }
}
