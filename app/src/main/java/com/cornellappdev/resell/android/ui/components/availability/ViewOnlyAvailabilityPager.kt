package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.components.availability.helper.AvailabilityPagerContainer
import com.cornellappdev.resell.android.ui.components.availability.helper.ViewOnlyAvailabilityGrid
import com.cornellappdev.resell.android.ui.components.availability.helper.testAvailabilities
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ViewOnlyAvailabilityPager(
    title: String,
    subtitle: String,
    availabilities: List<LocalDateTime>,
    onSelectAvailability: (LocalDateTime) -> Unit,
) {
    val startDate = availabilities.max().toLocalDate()
    AvailabilityPagerContainer(
        title = title,
        subtitle = subtitle,
        startDate = startDate,
        scrollRange = 6 to 0
    ) { dates, _ ->
        ViewOnlyAvailabilityGrid(
            dates,
            availabilities,
            onSelectAvailability = onSelectAvailability
        )
    }
}

@Composable
@Preview
private fun ViewOnlyAvailabilityPagerPreview() = ResellPreview {
    var selectedDate by remember { mutableStateOf(LocalDateTime.now()) }
    val availabilities = remember {
        testAvailabilities(buildList {
            repeat(5) {
                add(LocalDate.now().plusDays(it * 2L))
            }
        })
    }
    Column {
        Text("Selected date: $selectedDate")
        ViewOnlyAvailabilityPager(
            title = "Please fill this out! :D",
            subtitle = "Or else he will find you...",
            availabilities = availabilities
        ) { selectedDate = it }
    }
}
