package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ImageStateObserver
import com.cornellappdev.resell.android.viewmodel.observeListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FromSearchesViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    coilRepository: CoilRepository,
) : ResellViewModel<FromSearchesViewModel.FromSearchesUiState>(
    initialUiState = FromSearchesUiState(
        listings = listOf(),
        loadedState = ResellApiState.Success
    )
) {
    data class FromSearchesUiState(
        val loadedState: ResellApiState,
        val listings: List<Pair<String, List<ListingUi>>>,
    )

    private val listingObserver =
        ImageStateObserver<List<Pair<String, List<ListingUi>>>>(coilRepository, viewModelScope) { listings ->
            applyMutation { copy(listings = listings) }
        }

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigateToPdp(listing)
    }

    fun onBackPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.MAIN)
    }

    fun hideSearch(search: String) {
        resellPostRepository.editHiddenSearches(search)
    }

    init {
        resellPostRepository.getSearchHistory()
        asyncCollect(resellPostRepository.fromSearchedPosts) { response ->
            val groups = response.asSuccessOrNull()?.data ?: emptyList()
            listingObserver.observeListings(groups.flatMap { (_, listings) -> listings }) { flat ->
                val remaining = flat.iterator()
                groups.map { (search, listings) -> search to listings.map { remaining.next() } }
            }
            applyMutation { copy(loadedState = response.toResellApiState()) }
        }
    }
}