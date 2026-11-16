package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ImageState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ImageStateObserver
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
        val listings: List<Pair<String, List<Listing>>>,
        val images: Map<String, ImageState> = emptyMap(),
    )

    private val imageObserver = ImageStateObserver(coilRepository, viewModelScope) { images ->
        applyMutation { copy(images = images) }
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
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    listings = response.asSuccessOrNull()?.data ?: emptyList()
                )
            }
            imageObserver.observe(stateValue().listings.flatMap { (_, listings) -> listings.map { it.image } })
        }
    }
}