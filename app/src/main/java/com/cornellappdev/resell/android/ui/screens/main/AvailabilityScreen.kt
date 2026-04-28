package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.availability.helper.AvailabilityFilters
import com.cornellappdev.resell.android.ui.components.availability.helper.GridSelectionType
import com.cornellappdev.resell.android.ui.components.availability.helper.MonthCalendar
import com.cornellappdev.resell.android.ui.components.availability.helper.SelectableAvailabilityGrid
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.viewmodel.main.AvailabilityViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class AvailabilityPanel {
    NONE,
    FILTERS,
    CALENDAR
}

// TODO: need to test this Screen once upstream Screen is fully implemented and networked (ProfileScreen)
@Composable
fun AvailabilityScreen(
    availabilityViewModel: AvailabilityViewModel = hiltViewModel()
) {

    val availabilityUiState = availabilityViewModel.collectUiStateValue()

    val firstOfWeek = availabilityUiState.currentMonth.atDay(1)
    val dates: List<LocalDate> = (0..2).map { firstOfWeek.plusDays(it.toLong()) }

    // just some UI logic to allow for smooth transitions between panels expanding on the screen.
    var activePanel by remember { mutableStateOf(AvailabilityPanel.NONE) }
    val panelVisible = activePanel != AvailabilityPanel.NONE

    val panelBackgroundColor = Color(0xFFF7F3F9)
    var panelHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val gridOffsetY by animateDpAsState(
        targetValue = if (panelVisible) with(density) { panelHeightPx.toDp() } else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "availability grid offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ResellHeader(
                title = "Availability",
                leftPainter = R.drawable.ic_chevron_left,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (panelVisible) panelBackgroundColor else Color.White)
            ) {
                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(21.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hamburger),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                activePanel = if (activePanel == AvailabilityPanel.FILTERS) {
                                    AvailabilityPanel.NONE
                                } else {
                                    AvailabilityPanel.FILTERS
                                }
                            }
                    )
                    Text(
                        text = availabilityUiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM")),
                        style = Style.heading3,
                        modifier = Modifier.clickable {
                            activePanel = if (activePanel == AvailabilityPanel.CALENDAR) {
                                AvailabilityPanel.NONE
                            } else {
                                AvailabilityPanel.CALENDAR
                            }
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                SelectableAvailabilityGrid(
                    dates = dates,
                    selectedAvailabilities = availabilityUiState.selectedAvailabilities,
                    setSelectedAvailabilities = { availabilityViewModel.setSelectedAvailabilities(it) },
                    gridSelectionType = GridSelectionType.AVAILABILITY,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .offset(y = gridOffsetY),
                    onProposalSelected = {}
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = panelVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .onSizeChanged { panelHeightPx = it.height },
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(durationMillis = 250)
                    ) + fadeIn(animationSpec = tween(durationMillis = 250)),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(durationMillis = 250)
                    ) + fadeOut(animationSpec = tween(durationMillis = 250)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .background(panelBackgroundColor)
                    ) {
                        when (activePanel) {
                            AvailabilityPanel.CALENDAR -> MonthCalendar(
                                currentMonth = availabilityUiState.currentMonth,
                                selectedDates = dates,
                                onMonthChange = { availabilityViewModel.setCurrentMonth(it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            AvailabilityPanel.FILTERS -> AvailabilityFilters(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 320.dp),
                            )
                            AvailabilityPanel.NONE -> Unit
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White)
                    )
                )
        )

        ResellTextButton(
            text = "Save",
            onClick = { availabilityViewModel.saveAvailability() },
            state = ResellTextButtonState.ENABLED,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        )
    }
}

@Preview
@Composable
fun AvailabilityScreenPreview() {
    AvailabilityScreen()
}
