package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.profile.AvailabilityRepository
import com.cornellappdev.resell.android.model.api.UserAvailability
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val availabilityRepository: AvailabilityRepository
) : ResellViewModel<AvailabilityViewModel.AvailabilityUiState>(
    initialUiState = AvailabilityUiState()
) {

    data class AvailabilityUiState(
        val selectedAvailabilities: List<LocalDateTime> = emptyList(),
        val currentMonth: YearMonth = YearMonth.now(),

        // TODO: googleCalendarEnabled and availabilitySharingEnabled are not yet wired in.
        //  Need to check how/where it is in the backend
        val googleCalendarEnabled: Boolean = false,
        val availabilitySharingEnabled: Boolean = false,

        // TODO: subCalendars should come from Google Calendar API, not hardcoded.
        val subCalendars: List<String> = emptyList(),
        val enabledSubCalendars: Set<String> = emptySet(),

        val isLoading: Boolean = false,
        val saveSuccess: Boolean = false,
        val errorMessage: String? = null,
    )

    init {
        loadAvailability()
    }

    // grid interactions

    fun setSelectedAvailabilities(slots: List<LocalDateTime>) {
        applyMutation { copy(selectedAvailabilities = slots) }
    }

    fun setCurrentMonth(month: YearMonth) {
        applyMutation { copy(currentMonth = month) }
    }

    fun setGoogleCalendarEnabled(enabled: Boolean) {
        // TODO: may need an OAuth scope check before enabling
        applyMutation { copy(googleCalendarEnabled = enabled) }
    }

    fun setAvailabilitySharingEnabled(enabled: Boolean) {
        applyMutation { copy(availabilitySharingEnabled = enabled) }
    }

    fun setSubCalendarEnabled(calendarName: String, enabled: Boolean) {
        applyMutation {
            val updated = if (enabled) enabledSubCalendars + calendarName
            else enabledSubCalendars - calendarName
            copy(enabledSubCalendars = updated)
        }
    }

    private fun loadAvailability() {
        viewModelScope.launch {
            applyMutation { copy(isLoading = true) }
            try {
                val availability = availabilityRepository.getMyAvailability()
                applyMutation {
                    copy(
                        selectedAvailabilities = availability.toLocalDateTimes(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                applyMutation { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun saveAvailability() {
        viewModelScope.launch {
            applyMutation { copy(isLoading = true, saveSuccess = false) }
            try {
                availabilityRepository.updateAvailability(stateValue().selectedAvailabilities)
                applyMutation { copy(isLoading = false, saveSuccess = true) }
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
 */
private fun UserAvailability.toLocalDateTimes(): List<LocalDateTime> {
    return schedule.values.flatten().map { slot ->
        LocalDateTime.parse(slot.startDate, DateTimeFormatter.ISO_DATE_TIME)
    }
}