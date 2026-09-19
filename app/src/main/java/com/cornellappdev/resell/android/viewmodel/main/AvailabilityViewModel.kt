package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.profile.AvailabilityRepository
import com.cornellappdev.resell.android.ui.components.availability.helper.dayGroupContaining
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
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
        val visibleDates: List<LocalDate> = dayGroupContaining(LocalDate.now()),

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

    fun onBackPressed() {
        rootNavigationRepository.popBackStack()
    }

    // grid interactions

    /**
     * The grid only shows and edits 3 days at a time, so [windowSlots] only contains slots for
     * those days. Merge it in rather than overwriting [AvailabilityUiState.selectedAvailabilities]
     * outright, or every other day's saved availability would be lost.
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
            applyMutation { copy(isLoading = true, errorMessage = null) }
            try {
                val availability = availabilityRepository.getMyAvailability()
                applyMutation {
                    copy(
                        selectedAvailabilities = availability,
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