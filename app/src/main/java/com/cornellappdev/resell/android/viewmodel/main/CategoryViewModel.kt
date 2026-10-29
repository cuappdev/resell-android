package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.util.ScrollPageResult
import com.cornellappdev.resell.android.util.fetchNextScrollPage
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val CATEGORY_PAGE_SIZE = 10

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    savedStateHandle: SavedStateHandle,
    ) : ResellViewModel<CategoryViewModel.CategoryUiState>(
    initialUiState = CategoryUiState(
        loadedState = ResellApiState.Loading,
        listings = emptyList(),
        filter = ResellFilter(),
        page = 1,
        bottomLoading = false,
        endReached = false
    )
) {
    data class CategoryUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val filter: ResellFilter,
        val page: Int,
        val bottomLoading: Boolean,
        val endReached: Boolean
    )

    private var fetchJob: Job? = null

    init {
        val categoryName: String? = savedStateHandle["category"]
        val category = categoryName?.let { ResellFilter.FilterCategory.valueOf(it) }
        if (category != null) {
            val initialFilter = ResellFilter(categoriesSelected = listOf(category))
            applyMutation { copy(filter = initialFilter) }
            getPosts(initialFilter)
        }
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

    fun navigateToSubmitRequest() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_REQUEST)
    }

    private fun getPosts(filter: ResellFilter) {
        // Ignore initial call which does not have the category. Avoids race condition
        if (filter.categoriesSelected.isEmpty()) return

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val posts =
                    resellPostRepository.getFilteredPosts(filter, page = 1, limit = CATEGORY_PAGE_SIZE)
                applyMutation {
                    copy(
                        listings = posts.map { it.toListing() },
                        loadedState = ResellApiState.Success,
                        page = 1,
                        endReached = posts.isEmpty(),
                        bottomLoading = false
                    )
                }
            } catch (_: CancellationException) {
            } catch (_: Exception) {
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Error,
                        bottomLoading = false
                    )
                }
            }
        }
    }

    fun onScrollBottom() {
        val state = uiStateFlow.value
        if (state.bottomLoading || state.endReached || state.loadedState !is ResellApiState.Success) return

        applyMutation { copy(bottomLoading = true) }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val result = fetchNextScrollPage(state.page, state.listings) { nextPage ->
                resellPostRepository.getFilteredPosts(state.filter, page = nextPage, limit = CATEGORY_PAGE_SIZE)
            }
            when (result) {
                is ScrollPageResult.Success -> applyMutation {
                    copy(
                        listings = listings + result.newListings,
                        page = result.nextPage,
                        endReached = result.endReached,
                        bottomLoading = false
                    )
                }

                is ScrollPageResult.Error -> applyMutation { copy(bottomLoading = false) }
            }
        }
    }

    fun onFilterChanged(filter: ResellFilter) {
        if (filter != uiStateFlow.value.filter) {
            applyMutation {
                copy(
                    filter = filter,
                    loadedState = ResellApiState.Loading,
                    bottomLoading = false
                )
            }
            getPosts(filter)
        }
    }

    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }
}