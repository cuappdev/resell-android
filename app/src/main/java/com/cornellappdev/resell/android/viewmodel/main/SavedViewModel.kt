package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ImageState
import com.cornellappdev.resell.android.viewmodel.ImageStateObserver
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
        val listings: List<Listing>,
        val images: Map<String, ImageState> = emptyMap(),
    )

    private val imageObserver = ImageStateObserver(coilRepository, viewModelScope) { images ->
        applyMutation { copy(images = images) }
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
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    listings = response.asSuccessOrNull()?.data?.map { it.toListing() } ?: listOf()
                )
            }
            imageObserver.observe(stateValue().listings.map { it.image })

        }
    }
}
