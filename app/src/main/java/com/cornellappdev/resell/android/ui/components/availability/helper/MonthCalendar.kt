package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Style
import java.time.YearMonth
import java.time.LocalDate


@Composable
fun MonthCalendar(
    currentMonth: YearMonth,
    selectedDates: List<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val dayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    val sortedDates = selectedDates.sorted()
    val rangeStart = sortedDates.firstOrNull()
    val rangeEnd = sortedDates.lastOrNull()

    Column(
        modifier
            .background(color = Color(0xfff7f3f9))
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
                    color = Color(0xFF707070)
                )
            }
        }

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDayOfWeek + 1
                    val date = if (day in 1..daysInMonth) currentMonth.atDay(day) else null
                    val isSelected = date != null && date in selectedDates
                    val isRangeStart = date == rangeStart
                    val isRangeEnd = date == rangeEnd
                    val isMiddle = isSelected && !isRangeStart && !isRangeEnd

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color(0xffe4e0e8) else Color.Transparent,
                                shape = when {
                                    isRangeStart && isRangeEnd -> RoundedCornerShape(4.dp)
                                    isRangeStart -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                                    isRangeEnd -> RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                                    isMiddle -> RoundedCornerShape(0.dp)
                                    else -> RoundedCornerShape(0.dp)
                                }
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Text(text = "$day", style = Style.body2)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MonthCalendarPreview() {
    MonthCalendar(
        currentMonth = YearMonth.of(2026, 4),
        selectedDates = listOf(
            LocalDate.of(2026, 4, 16),
            LocalDate.of(2026, 4, 17),
            LocalDate.of(2026, 4, 18),
        ),
        onMonthChange = {}
    )
}