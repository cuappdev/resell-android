package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.profile.AvailabilityRepository
import com.cornellappdev.resell.android.model.api.UserAvailability
import com.cornellappdev.resell.android.ui.components.availability.helper.dayGroupContaining
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val availabilityRepository: AvailabilityRepository,
    private val rootNavigationRepository: RootNavigationRepository
) : ResellViewModel<AvailabilityViewModel.AvailabilityUiState>(
    initialUiState = AvailabilityUiState()
) {

    data class AvailabilityUiState(
        val selectedAvailabilities: Set<LocalDateTime> = emptySet(),
        val currentMonth: YearMonth = YearMonth.now(),
        val visibleDates: List<LocalDate> = dayGroupContaining(YearMonth.now().atDay(1)),

        val isLoading: Boolean = false,
        val saveSuccess: Boolean = false,
        val errorMessage: String? = null,
    )

    init {
        loadAvailability()
    }

    fun onBackPressed() {
        rootNavigationRepository.popBackStack()
    }

    // grid interactions

    /**
     * The grid only shows and edits 3 days at a time, so [windowSlots] only contains slots for
     * those days. Merge it in rather than overwriting [AvailabilityUiState.selectedAvailabilities]
     */
    fun setSelectedAvailabilities(windowSlots: Set<LocalDateTime>) {
        applyMutation {
            val visibleDateSet = visibleDates.toSet()
            val outsideWindow = selectedAvailabilities.filterNot { it.toLocalDate() in visibleDateSet }
            copy(selectedAvailabilities = outsideWindow.toSet() + windowSlots)
        }
    }

    fun setCurrentMonth(month: YearMonth) {
        applyMutation { copy(currentMonth = month, visibleDates = dayGroupContaining(month.atDay(1))) }
    }

    fun setVisibleDates(dates: List<LocalDate>) {
        applyMutation { copy(visibleDates = dates) }
    }

    private fun loadAvailability() {
        viewModelScope.launch {
            applyMutation { copy(isLoading = true, errorMessage = null) }
            try {
                val availability = availabilityRepository.getMyAvailability()
                applyMutation {
                    copy(
                        selectedAvailabilities = availability.toLocalDateTimes(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                applyMutation { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun saveAvailability() {
        viewModelScope.launch {
            applyMutation { copy(isLoading = true, saveSuccess = false, errorMessage = null) }
            try {
                availabilityRepository.updateAvailability(stateValue().selectedAvailabilities.toList())
                applyMutation { copy(isLoading = false, saveSuccess = true, errorMessage = null) }
            } catch (e: Exception) {
                applyMutation { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

/**
 * Converts the backend schedule (Map<dateString, List<AvailabilitySlot>>) back into
 * a flat list of LocalDateTimes for the grid to consume.
 * Each slot's startDate is used as the representative time for a cell.
 *
 * The backend sends startDate as a UTC instant (e.g. "2026-01-23T16:00:00.000Z"), so it's
 * parsed as an [Instant] and converted to the device's local time.
 */
private fun UserAvailability.toLocalDateTimes(): Set<LocalDateTime> {
    return schedule.values.flatten().map { slot ->
        Instant.parse(slot.startDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }.toSet()
}