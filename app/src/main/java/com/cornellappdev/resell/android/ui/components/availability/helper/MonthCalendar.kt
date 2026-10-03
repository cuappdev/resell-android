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
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.AvailabilityPanelBackground
import com.cornellappdev.resell.android.ui.theme.AvailabilitySelectedDate
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.Style
import java.time.YearMonth
import java.time.LocalDate


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
            .background(color = AvailabilityPanelBackground)
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

        // Capped to the dominant 5-row case; a rare 6-row month scrolls here instead of
        // growing past it, so the day-of-week header above stays pinned and the panel
        // never has to resize.
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
                        // Availability in the past can never be proposed, so past days are inert.
                        val isPast = date.isBefore(today)
                        val isSelected = date in selectedDates
                        // Rounding is per-row: a selected run can span multiple weeks or have
                        // gaps, so "start"/"end" must mean the edges of the run *in this row*,
                        // not the overall min/max of the whole selection.
                        val hasSelectedLeftNeighbor = col > 0 && date.minusDays(1) in selectedDates
                        val hasSelectedRightNeighbor = col < 6 && date.plusDays(1) in selectedDates
                        val isRowStart = isSelected && !hasSelectedLeftNeighbor
                        val isRowEnd = isSelected && !hasSelectedRightNeighbor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) AvailabilitySelectedDate else Color.Transparent,
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

/**
 * Interactive so left/right swipes to change month can be tested by hand. Past days grey out and
 * stop responding to taps, and the back-swipe dies once the current month is reached, so anchor
 * the preview on a month far enough ahead that the whole grid is live.
 */
@Preview
@Composable
fun MonthCalendarPreview() {
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
fun MonthCalendarThirtyOneDayMonthPreview() {
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

/** Showcase the 6-row edge case: should be scrollable. */
@Preview
@Composable
fun MonthCalendarSixRowMonthPreview() {
    MonthCalendar(
        currentMonth = YearMonth.of(2027, 5),
        selectedDates = emptyList(),
        onMonthChange = {},
        onDayStartSelected = {}
    )
}

/**
 * A 3-day group of Fri/Sat/Sun spans two calendar rows.
 * Friday and Sunday should each round on the outer edge of their own row (not backwards),
 * and Saturday, last in its row, with nothing selected after it, should round its right
 * edge too, instead of being treated as a seamless "middle" cell.
 */
@Preview
@Composable
fun MonthCalendarRowSpanningSelectionPreview() {
    MonthCalendar(
        currentMonth = YearMonth.of(2027, 5),
        selectedDates = dayWindowStartingAt(LocalDate.of(2027, 4, 30)),
        onMonthChange = {},
        onDayStartSelected = {}
    )
}