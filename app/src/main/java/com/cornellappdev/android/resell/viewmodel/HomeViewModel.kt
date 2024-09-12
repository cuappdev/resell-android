package com.cornellappdev.android.resell.viewmodel

import com.cornellappdev.android.resell.model.Listing
import com.cornellappdev.android.resell.util.richieListings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ResellViewModel<HomeViewModel.HomeUiState>(
    initialUiState = HomeUiState.Success(
        richieListings(40)
    )
) {

    sealed class HomeUiState {
        data object Loading : HomeUiState()
        data class Success(
            val listings: List<Listing>
        ) : HomeUiState()

        data object Error : HomeUiState()
    }

    fun onListingPressed(listing: Listing) {
        // TODO: Implement
    }

}
