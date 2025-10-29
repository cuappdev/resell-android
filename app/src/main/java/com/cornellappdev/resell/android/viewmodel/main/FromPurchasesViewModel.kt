package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FromPurchasesViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
) : ResellViewModel<FromPurchasesViewModel.FromPurchasesUiState>(
    initialUiState = FromPurchasesUiState(
        listings = listOf(),
        loadedState = ResellApiState.Success
    )
) {
    data class FromPurchasesUiState(
        val loadedState: ResellApiState,
        val listings: List<Pair<String, List<Listing>>>,
    )

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigateToPdp(listing)
    }

    fun onBackPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.MAIN)
    }
}