package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.viewmodel.ImageStateObserver
import com.cornellappdev.resell.android.viewmodel.observeListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    coilRepository: CoilRepository,
) :
    ResellViewModel<SavedViewModel.SavedUiState>(
        initialUiState = SavedUiState(
            listings = listOf(),
            loadedState = ResellApiState.Success
        )
    ) {

    data class SavedUiState(
        val loadedState: ResellApiState,
        val listings: List<ListingUi>,
    )

    private val listingObserver = ImageStateObserver<List<ListingUi>>(coilRepository, viewModelScope) { listings ->
        applyMutation { copy(listings = listings) }
    }

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigateToPdp(listing)
    }

    fun onLoad() {
        resellPostRepository.fetchSavedPosts()
    }

    init {
        onLoad()

        asyncCollect(resellPostRepository.savedPosts) { response ->
            listingObserver.observeListings(
                response.asSuccessOrNull()?.data?.map { it.toListing() } ?: listOf()
            )
            applyMutation { copy(loadedState = response.toResellApiState()) }
        }
    }
}
