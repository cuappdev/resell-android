package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.AvailabilityPanelBackgroundColor
import com.cornellappdev.resell.android.ui.theme.AvailabilitySelectedDateColor
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import java.time.YearMonth
import java.time.LocalDate

/** Controls drag distance for swiping between months */
val MonthSwipeThreshold: Dp = 56.dp


/** Caps [MonthCalendar]'s day-grid at the height of 5 rows (most common) */
val MonthCalendarGridMaxHeight: Dp = 248.dp

@Composable
fun MonthCalendar(
    currentMonth: YearMonth,
    selectedDates: List<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDayStartSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }
    val thisMonth = remember(today) { YearMonth.from(today) }
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val dayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    val swipeThresholdPx = with(LocalDensity.current) { MonthSwipeThreshold.toPx() }

    Column(
        modifier
            .background(color = AvailabilityPanelBackgroundColor)
            .pointerInput(currentMonth) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            totalDrag <= -swipeThresholdPx -> onMonthChange(currentMonth.plusMonths(1))
                            totalDrag >= swipeThresholdPx && currentMonth.isAfter(thisMonth) ->
                                onMonthChange(currentMonth.minusMonths(1))
                        }
                        totalDrag = 0f
                    },
                    onDragCancel = { totalDrag = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    totalDrag += dragAmount
                }
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = Style.body2,
                    fontWeight = FontWeight.SemiBold,
                    color = AppDev
                )
            }
        }

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        // Calendar displays with 6 rows become scrollable
        Column(
            modifier = Modifier
                .heightIn(max = MonthCalendarGridMaxHeight)
                .verticalScroll(rememberScrollState())
        ) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val date = firstDayOfMonth.plusDays((cellIndex - firstDayOfWeek).toLong())
                        val isInCurrentMonth = YearMonth.from(date) == currentMonth
                        val isPast = date.isBefore(today)
                        val isSelected = date in selectedDates
                        val hasSelectedLeftNeighbor = col > 0 && date.minusDays(1) in selectedDates
                        val hasSelectedRightNeighbor = col < 6 && date.plusDays(1) in selectedDates
                        val isRowStart = isSelected && !hasSelectedLeftNeighbor
                        val isRowEnd = isSelected && !hasSelectedRightNeighbor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) AvailabilitySelectedDateColor else Color.Transparent,
                                    shape = when {
                                        isRowStart && isRowEnd -> RoundedCornerShape(4.dp)
                                        isRowStart -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                                        isRowEnd -> RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                                        else -> RoundedCornerShape(0.dp)
                                    }
                                )
                                .then(
                                    if (isPast) Modifier
                                    else Modifier.clickable { onDayStartSelected(date) }
                                )
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${date.dayOfMonth}",
                                style = Style.body2,
                                color = if (isPast || !isInCurrentMonth) IconInactive
                                else Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MonthCalendarPreview() = ResellPreview {
    var currentMonth by remember { mutableStateOf(YearMonth.now().plusMonths(1)) }
    var selectedDates by remember {
        mutableStateOf(dayWindowStartingAt(YearMonth.now().plusMonths(1).atDay(16)))
    }
    MonthCalendar(
        currentMonth = currentMonth,
        selectedDates = selectedDates,
        onMonthChange = { currentMonth = it },
        onDayStartSelected = { selectedDates = dayWindowStartingAt(it) }
    )
}

/** Testing display for month with 31 days, and a window rolling off its end into the next. */
@Preview
@Composable
fun MonthCalendarThirtyOneDayMonthPreview() = ResellPreview {
    val month = YearMonth.of(2027, 7)
    var selectedDates by remember {
        mutableStateOf(dayWindowStartingAt(LocalDate.of(2027, 7, 31)))
    }
    MonthCalendar(
        currentMonth = month,
        selectedDates = selectedDates,
        onMonthChange = {},
        onDayStartSelected = { selectedDates = dayWindowStartingAt(it) }
    )
}

/** Testing 6-row edge case (scrollable) */
@Preview
@Composable
fun MonthCalendarSixRowMonthPreview() = ResellPreview {
    MonthCalendar(
        currentMonth = YearMonth.of(2027, 5),
        selectedDates = emptyList(),
        onMonthChange = {},
        onDayStartSelected = {}
    )
}

/**
 * When a grouping of 3 spans 2 rows, with the middle element
 * being on the first row, the middle element should be rounded
 * correctly (not treated as a border, treated as a middle element)
 */
@Preview
@Composable
fun MonthCalendarRowSpanningSelectionPreview() = ResellPreview {
    MonthCalendar(
        currentMonth = YearMonth.of(2027, 5),
        selectedDates = dayWindowStartingAt(LocalDate.of(2027, 4, 30)),
        onMonthChange = {},
        onDayStartSelected = {}
    )
}