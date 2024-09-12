package com.cornellappdev.android.resell.viewmodel

import com.cornellappdev.android.resell.model.Listing
import com.cornellappdev.android.resell.model.ResellApiState
import com.cornellappdev.android.resell.util.richieListings
import com.cornellappdev.android.resell.viewmodel.HomeViewModel.HomeFilter
import com.cornellappdev.android.resell.viewmodel.HomeViewModel.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor() :
    ResellViewModel<SavedViewModel.SavedUiState>(
        initialUiState = SavedUiState(
            listings = richieListings(40),
            loadedState = ResellApiState.Success
        )
    ) {

    data class SavedUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
    )

    fun onListingPressed(listing: Listing) {
        // TODO: Implement
    }
}
