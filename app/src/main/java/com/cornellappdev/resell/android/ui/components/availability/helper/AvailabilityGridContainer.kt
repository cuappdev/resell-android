package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Style
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val GRID_HOURS = GRID_HEIGHT * SLOT_DURATION_MINUTES / 60

@Composable
private fun TimeColumn() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 16.dp)
    ) {
        val hourHeight = maxHeight / GRID_HOURS
        repeat(GRID_HOURS) { hour ->
            Text(
                getTimeForRow(hour).format(DateTimeFormatter.ofPattern("hh:mm a")),
                style = Style.title2,
                modifier = Modifier.offset(y = hourHeight * hour)
            )
        }
    }
}

@Composable
private fun DateRow(dates: List<LocalDate>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        dates.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    it.format(DateTimeFormatter.ofPattern("E")),
                    style = Style.title1
                )
                Text(
                    it.format(DateTimeFormatter.ofPattern("MMM d")),
                    style = Style.body1
                )
            }
        }
    }
}

@Composable
fun AvailabilityGridContainer(
    dates: List<LocalDate>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier) {
        DateRow(
            dates,
            modifier = Modifier
                // Sorry for hardcoding this, but otherwise I would have to use a constraint layout
                //  because the position of the dates depends on the width of the times and the
                //  position of the times depends on the height of the dates. The text size and
                //  padding is fixed so it shouldn't be a problem.
                .padding(start = 90.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.weight(GRID_HEIGHT.toFloat() / 2)) {
            TimeColumn()
            Box(modifier = Modifier.weight(dates.size.toFloat())) {
                content()
            }
        }
    }
}
