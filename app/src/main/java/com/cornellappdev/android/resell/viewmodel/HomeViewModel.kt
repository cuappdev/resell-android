package com.cornellappdev.android.resell.viewmodel

import com.cornellappdev.android.resell.model.Listing
import com.cornellappdev.android.resell.model.ResellApiState
import com.cornellappdev.android.resell.util.richieListings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = richieListings(40),
            filters = listOf(),
            loadedState = ResellApiState.Success
        )
    ) {

    data class HomeUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val filters: List<HomeFilter>,
    )

    enum class HomeFilter {
        RECENT, CLOTHING, BOOKS, SCHOOL, ELECTRONICS, HOUSEHOLD, HANDMADE, SPORTS, OTHER
    }

    fun onListingPressed(listing: Listing) {
        // TODO: Implement
    }

    fun onToggleFilter(filter: HomeFilter) {
        applyMutation {
            if (filters.contains(filter)) {
                copy(filters = filters - filter)
            } else {
                copy(filters = filters + filter)
            }
        }
    }
}
