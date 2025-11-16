package com.cornellappdev.resell.android.viewmodel.pdp

import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoldViewModel @Inject constructor(
    soldRepository: SoldRepository
) : ResellViewModel<SoldViewModel.SoldUiState>(
    initialUiState = SoldUiState()
) {
    data class SoldUiState(
        val showing: Boolean = false
    )

    fun onShow() {
        applyMutation { copy(showing = true) }
    }

    fun onUnShow() {
        applyMutation { copy(showing = false) }
    }

    init {
        asyncCollect(soldRepository.showSoldEvent) { event ->
            event?.consume {
                applyMutation {
                    copy (
                        showing = true
                    )
                }
            }
        }
    }
}