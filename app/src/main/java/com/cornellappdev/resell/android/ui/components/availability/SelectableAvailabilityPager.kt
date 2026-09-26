package com.cornellappdev.resell.android.ui.components.availability

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
import com.cornellappdev.resell.android.ui.components.availability.helper.DAY_WINDOW_SIZE
import com.cornellappdev.resell.android.ui.components.availability.helper.GridSelectionType
import com.cornellappdev.resell.android.ui.components.availability.helper.SelectableAvailabilityGrid
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun SelectableAvailabilityPager(
    title: String,
    subtitle: String,
    initialSelectedAvailabilities: List<LocalDateTime> = emptyList(),
    availableAvailabilities: List<LocalDateTime>? = null,
    scrollRange: Pair<Int, Int> = 0 to 6,
    modifier: Modifier = Modifier,
    gridSelectionType: GridSelectionType,
    onEditAvailabilityClicked: (() -> Unit)? = null,
    setProposalTime: (LocalDateTime) -> Unit,
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
        //  Each page corresponds to an increment of DAY_WINDOW_SIZE days, and each
        //  inner list corresponds the availabilities for that window.
        //  Thus, we must add to the correct window.
        initialSelectedAvailabilities.forEach { availability ->
            val today = LocalDate.now()
            // Period.days is the day-of-month remainder, not elapsed days, so it puts an
            // availability on the wrong page as soon as it crosses a month boundary.
            val dayDifference = ChronoUnit.DAYS.between(today, availability.toLocalDate()).toInt()
            val pageIndex = Math.floorDiv(dayDifference, DAY_WINDOW_SIZE)

            if (selectedDatesByPage[pageIndex] != null) {
                val list = selectedDatesByPage[pageIndex]!!.toMutableList()
                list.add(availability)
                val copy = selectedDatesByPage.toMutableMap()
                copy[pageIndex] = list
                selectedDatesByPage = copy.toMap()
            }
        }
    }

    AvailabilityPagerContainer(
        startDate = LocalDate.now(),
        scrollRange = scrollRange,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        onEditAvailabilityClicked = onEditAvailabilityClicked,
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
            },
            gridSelectionType = gridSelectionType,
            availableAvailabilities = availableAvailabilities,
            onProposalSelected = setProposalTime
        )
    }
}

@Preview
@Composable
private fun SelectableAvailabilityPagerPreview() = ResellPreview {
    var selectedAvailabilities by remember { mutableStateOf(emptyList<LocalDateTime>()) }
    var proposedAvailabilities: LocalDateTime? by remember { mutableStateOf(null) }
    Column {
        Text("Selected availabilities = $selectedAvailabilities")
        Text("Proposed availability = $proposedAvailabilities")
        SelectableAvailabilityPager(
            title = "When are you free?",
            subtitle = "Fill it out or else.",
            gridSelectionType = GridSelectionType.AVAILABILITY,
            setProposalTime = {
                proposedAvailabilities = it
            }
        ) { selectedAvailabilities = it }
    }
}
