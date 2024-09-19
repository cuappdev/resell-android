package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.model.Listing
import com.cornellappdev.resell.android.model.ResellApiState
import com.cornellappdev.resell.android.util.richieListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = richieListings(40),
            activeFilter = HomeFilter.RECENT,
            loadedState = ResellApiState.Success
        )
    ) {

    data class HomeUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val activeFilter: HomeFilter,
    )

    enum class HomeFilter {
        RECENT, CLOTHING, BOOKS, SCHOOL, ELECTRONICS, HOUSEHOLD, HANDMADE, SPORTS, OTHER
    }

    fun onListingPressed(listing: Listing) {
        // TODO: Implement
    }

    fun onToggleFilter(filter: HomeFilter) {
        applyMutation {
            copy(activeFilter = filter)
        }
    }
}
