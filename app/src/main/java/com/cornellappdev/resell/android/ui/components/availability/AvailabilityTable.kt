package com.cornellappdev.resell.android.ui.components.availability

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import java.time.LocalDateTime

@Composable
fun AvailabilityTable(availabilities: List<Pair<LocalDateTime, LocalDateTime>>) {
    val state = rememberPagerState { 0 }  // TODO make it based on availabilities
    HorizontalPager(state = state) { page ->
        // TODO going to explore Jetlagged for a bit for inspiration
    }
}

@Preview
@Composable
private fun AvailabilityTablePreview() = ResellPreview {
    AvailabilityTable(emptyList())
}