package com.cornellappdev.resell.android.viewmodel.newpost

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.RootSheet
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsEntryViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val rootNavigationRepository: RootNavigationRepository,
) : ResellViewModel<PostDetailsEntryViewModel.PostEntryUiState>(
    initialUiState = PostEntryUiState()
) {

    data class PostEntryUiState(
        val title: String = "",
        val description: String = "",
        val price: String = "",
        val activeFilter: HomeViewModel.HomeFilter = HomeViewModel.HomeFilter.CLOTHING,
        val loadingPost: Boolean = false,
    ) {
        private val canConfirm
            get() = title.isNotBlank() && price.isNotBlank()

        val buttonState: ResellTextButtonState
            get() = if (loadingPost) {
                ResellTextButtonState.SPINNING
            } else if (canConfirm) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(price = it)
                    }
                },
                confirmString = "Set Price",
                title = "What price do you want to propose?",
                defaultPrice = stateValue().price
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

    fun onFilterPressed(filter: HomeViewModel.HomeFilter) {
        applyMutation {
            copy(activeFilter = filter)
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
