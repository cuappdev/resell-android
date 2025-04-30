package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
) : ResellViewModel<CategoryViewModel.CategoryUiState>(
    initialUiState = CategoryUiState(
        loadedState = ResellApiState.Loading,
        listings = emptyList(),
        filter = ResellFilter()
    )
) {
    data class CategoryUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val filter: ResellFilter
    )

    init {
        getPosts(ResellFilter())
    }

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigate(
            ResellRootRoute.PDP(
                userImageUrl = listing.user.imageUrl,
                username = listing.user.username,
                userId = listing.user.id,
                userHumanName = listing.user.name,
                listingJson = Json.encodeToString(listing)
            )
        )
    }

    private fun getPosts(filter: ResellFilter) {
        // Ignore initial call which does not have the category. Avoids race condition
        if (filter.categoriesSelected.isEmpty()) return

        viewModelScope.launch {
            try {
                val posts = resellPostRepository.getFilteredPosts(filter)
                applyMutation {
                    copy(
                        listings = posts.map { it.toListing() },
                        loadedState = ResellApiState.Success
                    )
                }
            } catch (e: Exception) {
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Error
                    )
                }
            }
        }
    }

    fun onFilterChanged(filter: ResellFilter) {
        if (filter != uiStateFlow.value.filter) {
            applyMutation {
                copy(
                    filter = filter,
                    loadedState = ResellApiState.Loading,
                )
            }
            getPosts(filter)
        }
    }

    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }
}