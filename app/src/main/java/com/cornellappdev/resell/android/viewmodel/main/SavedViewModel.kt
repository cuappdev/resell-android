package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.domain.GetListingUisUseCase
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    private val getListingUis: GetListingUisUseCase,
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

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigateToPdp(listing)
    }

    fun onLoad() {
        resellPostRepository.fetchSavedPosts()
    }

    init {
        onLoad()

        val savedListings = resellPostRepository.savedPosts.flatMapLatest { response ->
            getListingUis(response.map { posts -> posts.map { it.toListing() } })
        }
        asyncCollect(savedListings) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    listings = response.asSuccessOrNull()?.data.orEmpty()
                )
            }
        }
    }
}
