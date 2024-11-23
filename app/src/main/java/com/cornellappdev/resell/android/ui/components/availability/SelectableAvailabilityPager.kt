package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.components.availability.helper.AvailabilityPagerContainer
import com.cornellappdev.resell.android.ui.components.availability.helper.SelectableAvailabilityGrid
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun SelectableAvailabilityPager(
    initialSelectedAvailabilities: List<LocalDateTime> = emptyList(),
    scrollRange: Pair<Int, Int> = 0 to 6,
    setSelectedAvailabilities: (List<LocalDateTime>) -> Unit,
) {
    var selectedDatesByPage by remember {
        mutableStateOf(buildList<List<LocalDateTime>> {
            repeat(scrollRange.second + 1) {
                add(emptyList())
            }
        })
    }

    LaunchedEffect(initialSelectedAvailabilities) {
        // TODO Use initialSelectedAvailabilities.
    }

    AvailabilityPagerContainer(
        startDate = LocalDate.now(),
        scrollRange = scrollRange,
    ) { dates, page ->
        SelectableAvailabilityGrid(
            dates,
            selectedAvailabilities = selectedDatesByPage[page],
            setSelectedAvailabilities = { availabilities ->
                val updatedDates = selectedDatesByPage.mapIndexed { index, localDateTimes ->
                    if (index != page) {
                        localDateTimes
                    } else {
                        availabilities
                    }
                }
                selectedDatesByPage = updatedDates
                setSelectedAvailabilities(updatedDates.flatten())
            })
    }
}

@Preview
@Composable
private fun SelectableAvailabilityPagerPreview() = ResellPreview {
    var selectedAvailabilities by remember { mutableStateOf(emptyList<LocalDateTime>()) }
    Column {
        Text("Selected availabilities = $selectedAvailabilities")
        SelectableAvailabilityPager { selectedAvailabilities = it }
    }
}
