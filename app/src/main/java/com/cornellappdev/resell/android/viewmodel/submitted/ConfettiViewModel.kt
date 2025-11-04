package com.cornellappdev.resell.android.viewmodel.submitted

import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfettiViewModel @Inject constructor(
    confettiRepository: ConfettiRepository
) :
    ResellViewModel<ConfettiViewModel.ConfettiUiState>(
        initialUiState = ConfettiUiState()
    ) {
    data class ConfettiUiState(
        val showing: Boolean = false
    )

    fun onShow() {
        applyMutation { copy(showing = true) }
    }

    fun onAnimationFinished() {
        applyMutation { copy(showing = false) }
    }

    init {
        asyncCollect(confettiRepository.showConfettiEvent) { event ->
            event?.consume {
                applyMutation {
                    copy(
                        showing = true
                    )
                }
            }
        }
    }
}
