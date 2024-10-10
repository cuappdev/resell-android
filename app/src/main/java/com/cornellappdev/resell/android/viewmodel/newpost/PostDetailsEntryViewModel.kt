package com.cornellappdev.resell.android.viewmodel.newpost

import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailsEntryViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun onPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {},
                confirmString = "Set Price",
                title = "What price do you want to propose?",
                defaultPrice = ""
            )
        )
    }
}
