package com.cornellappdev.resell.android.ui.components.global.sheet

import com.cornellappdev.resell.android.util.formatMoney
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val NUM_DIGITS = 4
const val MAX_PRICE = 1000.0

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
                    confirmText = uiEvent.payload.confirmString,
                    price = uiEvent.payload.defaultPrice,
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
            get() = enabled &&
                    (!hasDot && price.length <= (NUM_DIGITS - 1) || hasDot && price
                        .substringAfter(
                            delimiter = ".", missingDelimiterValue = ""
                        ).length < 2)

        fun canAppend(number: String): Boolean {
            if (!canPressNumber) return false
            if (price.isEmpty() && number == "0") return false
            val newPrice = price + number
            return newPrice.toDoubleOrNull()?.let { it <= MAX_PRICE } ?: false
        }

        val canPressAnyNumber
            get() = (0..9).any { canAppend(it.toString()) }

        val canPressDot
            get() = enabled && !hasDot

        val canPressDelete
            get() = enabled && price.isNotEmpty()

        val canConfirm
            get() = enabled && price.isNotEmpty() &&
                    (!hasDot || price.substringAfter(".", "").length == 2)
    }


    fun onNumberPressed(number: String) {
        if (!stateValue().canAppend(number)) {
            return
        }

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
        stateValue().callback(stateValue().price.formatMoney())

        applyMutation {
            copy(
                enabled = false,
            )
        }
    }

    fun onDeletePressed() {
        if (stateValue().price.isNotEmpty()) {
            if (stateValue().price.dropLast(1) == "0") {
                applyMutation {
                    copy(
                        price = ""
                    )
                }

                return
            }

            applyMutation {
                copy(
                    price = stateValue().price.dropLast(1)
                )
            }
        }
    }
}
