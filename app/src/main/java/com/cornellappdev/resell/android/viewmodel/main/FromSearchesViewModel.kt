package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.domain.GetImageStatesUseCase
import com.cornellappdev.resell.android.domain.withImages
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FromSearchesViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    private val getImageStates: GetImageStatesUseCase,
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
        val searches = resellPostRepository.fromSearchedPosts.flatMapLatest { response ->
            val allListings = response.asSuccessOrNull()?.data.orEmpty().flatMap { it.second }
            getImageStates(allListings.map { it.image }).map { images ->
                response.map { searches ->
                    searches.map { (search, listings) -> search to listings.withImages(images) }
                }
            }
        }
        asyncCollect(searches) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    listings = response.asSuccessOrNull()?.data.orEmpty()
                )
            }
        }
    }
}