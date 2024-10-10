package com.cornellappdev.resell.android.ui.components.global.sheet

import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PriceProposalSheetViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
) : ResellViewModel<PriceProposalSheetViewModel.PriceProposalStateUi>(
    initialUiState = PriceProposalStateUi()
) {

    init {
        asyncCollect(rootNavigationSheetRepository.rootSheetFlow) { uiEvent ->
            if (uiEvent == null || uiEvent.payload !is RootSheet.ProposalSheet) {
                return@asyncCollect
            }

            applyMutation {
                copy(
                    enabled = true,
                    callback = uiEvent.payload.callback,
                    title = uiEvent.payload.title,
                    confirmText = uiEvent.payload.confirmString
                )
            }
        }
    }

    data class PriceProposalStateUi(
        val enabled: Boolean = false,
        val price: String = "",
        val confirmText: String = "Confirm",
        val title: String = "",
        val callback: (price: String) -> Unit = {},
    ) {
        private val hasDot
            get() = price.contains(".")

        val canPressNumber
            get() = enabled && !hasDot && price.length <= 5 || hasDot && price.length <= 7

        val canPressDot
            get() = enabled && !hasDot

        val canPressDelete
            get() = enabled && price.isNotEmpty()

        val canConfirm
            get() = enabled && price.isNotEmpty() &&
                    (!hasDot || price.substringAfter(".", "").length == 2)
    }


    fun onNumberPressed(number: String) {
        applyMutation {
            copy(
                price = price + number
            )
        }
    }

    fun onDotPressed() {
        applyMutation {
            copy(
                price = if (price.isEmpty()) {
                    "0."
                } else {
                    "$price."
                }
            )
        }
    }

    fun onConfirmPressed() {
        // Hide the sheet and fire the callback.
        rootNavigationSheetRepository.hideSheet()
        stateValue().callback(stateValue().price)

        applyMutation {
            copy(
                enabled = false,
            )
        }
    }

    fun onDeletePressed() {
        if (stateValue().price.isNotEmpty()) {
            applyMutation {
                copy(
                    price = stateValue().price.dropLast(1)
                )
            }
        }
    }
}
