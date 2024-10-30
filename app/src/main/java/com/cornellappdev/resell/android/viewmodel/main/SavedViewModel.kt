package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.util.richieListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
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
