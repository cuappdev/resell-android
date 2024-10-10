package com.cornellappdev.resell.android.viewmodel.newpost

import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.RootSheet
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailsEntryViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository
) : ResellViewModel<PostDetailsEntryViewModel.PostEntryUiState>(
    initialUiState = PostEntryUiState()
) {

    data class PostEntryUiState(
        val title: String = "",
        val description: String = "",
        val price: String = "",
        val activeFilter: HomeViewModel.HomeFilter = HomeViewModel.HomeFilter.CLOTHING
    )

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
}
