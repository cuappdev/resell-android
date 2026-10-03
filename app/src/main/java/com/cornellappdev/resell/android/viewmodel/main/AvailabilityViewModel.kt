package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.profile.AvailabilityRepository
import com.cornellappdev.resell.android.ui.components.availability.helper.dayWindowStartingAt
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
        val visibleDates: List<LocalDate> = dayWindowStartingAt(LocalDate.now()),

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

    /**
     * Past availability can never be proposed, so a month before the current one is rejected
     * outright and the current month anchors on today rather than on the 1st.
     */
    fun setCurrentMonth(month: YearMonth) {
        val today = LocalDate.now()
        if (month.isBefore(YearMonth.from(today))) return
        val anchor = maxOf(month.atDay(1), today)
        applyMutation { copy(currentMonth = month, visibleDates = dayWindowStartingAt(anchor)) }
    }

    /**
     * [date] becomes the leftmost column of the grid, clamped forward to today so the window
     * never backfills. [AvailabilityUiState.currentMonth] is deliberately left alone: tapping a
     * trailing day of an adjacent month shouldn't reshuffle the calendar panel under the user.
     */
    fun setWindowStart(date: LocalDate) {
        val anchor = maxOf(date, LocalDate.now())
        applyMutation { copy(visibleDates = dayWindowStartingAt(anchor)) }
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