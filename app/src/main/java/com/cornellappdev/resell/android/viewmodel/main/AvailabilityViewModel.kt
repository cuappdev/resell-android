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
        val today = LocalDate.now()
        if (month.isBefore(YearMonth.from(today))) return
        val anchor = maxOf(month.atDay(1), today)
        applyMutation { copy(currentMonth = month, visibleDates = dayWindowStartingAt(anchor)) }
    }

    fun setWindowStart(date: LocalDate) {
        val anchor = maxOf(date, LocalDate.now())
        applyMutation { copy(visibleDates = dayWindowStartingAt(anchor)) }
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