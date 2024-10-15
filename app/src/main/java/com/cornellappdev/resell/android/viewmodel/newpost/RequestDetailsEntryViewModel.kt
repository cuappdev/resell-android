package com.cornellappdev.resell.android.viewmodel.newpost

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.isLeqMoney
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.RootSheet
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailsEntryViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository
) : ResellViewModel<RequestDetailsEntryViewModel.RequestDetailsEntryViewState>(
    initialUiState = RequestDetailsEntryViewState()
) {

    data class RequestDetailsEntryViewState(
        val title: String = "",
        val description: String = "",
        val minPrice: String = "",
        val maxPrice: String = "",
        val loadingPost: Boolean = false,
    ) {
        private val canConfirm
            get() = title.isNotBlank() && minPrice.isNotBlank() && maxPrice.isNotBlank()
                    && minPrice.isLeqMoney(maxPrice)

        val buttonState: ResellTextButtonState
            get() = if (loadingPost) {
                ResellTextButtonState.SPINNING
            } else if (canConfirm) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onMinPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(minPrice = it)
                    }
                },
                confirmString = "Set Min Price",
                title = "What minimum price do you want to propose?",
                defaultPrice = stateValue().minPrice
            )
        )
    }

    fun onMaxPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(maxPrice = it)
                    }
                },
                confirmString = "Set Max Price",
                title = "What maximum price do you want to propose?",
                defaultPrice = stateValue().maxPrice
            )
        )
    }

    fun onTitleChanged(title: String) {
        applyMutation {
            copy(title = title)
        }
    }

    fun onDescriptionChanged(description: String) {
        applyMutation {
            copy(description = description)
        }
    }

    fun onConfirmPost() {
        // TODO: Placeholder
        viewModelScope.launch {
            applyMutation {
                copy(loadingPost = true)
            }
            delay(2000)
            applyMutation {
                copy(loadingPost = false)
            }
            rootNavigationRepository.navigate(ResellRootRoute.MAIN)
        }
    }
}
