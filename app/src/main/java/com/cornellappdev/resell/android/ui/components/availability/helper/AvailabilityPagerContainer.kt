package com.cornellappdev.resell.android.ui.components.availability.helper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * @param startDate the initial date that shows in the leftmost column of the grid
 * @param scrollRange the first value is the maximum number of times the user can scroll can press
 * the scroll left button from the start page until they run out of pages, and the second value is
 * the maximum number of times the user can press the scroll right button from the start page.
 * Example: scrollRange = 1 to 1, the user starts at startDate, can scroll 1 page to the left, and 1
 * page ot the right.
 * @param availabilityGrid the grid to display in the pager
 */
@Composable
fun AvailabilityPagerContainer(
    title: String,
    subtitle: String,
    startDate: LocalDate,
    scrollRange: Pair<Int, Int>,
    modifier: Modifier = Modifier,
    availabilityGrid: @Composable (dates: List<LocalDate>, page: Int) -> Unit,
) {
    val state =
        rememberPagerState(initialPage = scrollRange.first) { scrollRange.first + scrollRange.second + 1 }
    val coroutineScope = rememberCoroutineScope()
    val iconSize = 24.dp

    val alphaLeft by animateFloatAsState(
        targetValue = if (state.currentPage == scrollRange.first) 0f else 1f,
        label = "alpha left arrow"
    )

    val alphaRight by animateFloatAsState(
        targetValue = if (state.currentPage == scrollRange.first + scrollRange.second) 0f else 1f,
        label = "alpha right arrow"
    )

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "Scroll left",
                modifier = Modifier
                    .clickableNoIndication {
                        coroutineScope.launch {
                            state.animateScrollToPage(state.currentPage - 1)
                        }
                    }
                    .size(iconSize)
                    .alpha(alphaLeft)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, style = Style.heading3)
                Text(
                    text = subtitle,
                    style = Style.body2,
                    color = Secondary
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = "Scroll right",
                modifier = Modifier
                    .clickableNoIndication {
                        coroutineScope.launch {
                            state.animateScrollToPage(state.currentPage + 1)
                        }
                    }
                    .size(iconSize)
                    .alpha(alphaRight)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        HorizontalPager(
            state, userScrollEnabled = false
        ) { page ->
            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                availabilityGrid(
                    buildList {
                        val offset = page - scrollRange.first
                        val displayedStartDate = startDate.plusDays(offset * 3L)
                        add(displayedStartDate)
                        add(displayedStartDate.plusDays(1))
                        add(displayedStartDate.plusDays(2))
                    },
                    page
                )
            }
        }
    }
}
