package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.domain.GetListingUisUseCase
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.util.ScrollPageResult
import com.cornellappdev.resell.android.util.fetchNextScrollPage
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val HOME_PAGE_SIZE = 10

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    private val getListingUis: GetListingUisUseCase
) :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = emptyList(),
            savedListings = emptyList(),
            activeFilter = ResellFilter(sortBy = ResellFilter.SortBy.NEWLY_LISTED),
            loadedState = ResellApiState.Loading,
            page = 1,
            bottomLoading = false,
            endReached = false,
            isDefaultFeed = true
        )
    ) {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    data class HomeUiState(
        val loadedState: ResellApiState,
        val listings: List<ListingUi>,
        val savedListings: List<ListingUi>,
        val activeFilter: ResellFilter,
        val page: Int,
        val bottomLoading: Boolean,
        val endReached: Boolean,
        val isDefaultFeed: Boolean,
    )

    private var fetchJob: Job? = null

    private val feedListings = MutableStateFlow<List<Listing>>(emptyList())

    init {
        asyncCollect(feedListings.flatMapLatest { getListingUis(it) }) { listings ->
            applyMutation { copy(listings = listings) }
        }

        getNewlyListedPosts()
        resellPostRepository.fetchSavedPosts()
        asyncCollect(resellPostRepository.savedPosts) { response ->
            applyMutation { copy(loadedState = response.toResellApiState()) }
        }

        // Kept apart from the collector above so that each saved image that loads doesn't also
        // reset loadedState, which the feed shares.
        val savedListings = resellPostRepository.savedPosts.flatMapLatest { response ->
            getListingUis(response.asSuccessOrNull()?.data?.map { it.toListing() }.orEmpty())
        }
        asyncCollect(savedListings) { listings ->
            applyMutation { copy(savedListings = listings) }
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

    private fun getPosts(filter: ResellFilter) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val posts = resellPostRepository.getFilteredPosts(filter, page = 1, limit = HOME_PAGE_SIZE)
                feedListings.value = posts.map { it.toListing() }
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Success,
                        page = 1,
                        endReached = posts.isEmpty(),
                        isDefaultFeed = false,
                        bottomLoading = false
                    )
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching filtered posts: ", e)
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Error,
                        bottomLoading = false
                    )
                }
            }
        }
    }

    private fun getNewlyListedPosts() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val posts = resellPostRepository.getNewlyListedPosts(page = 1, limit = HOME_PAGE_SIZE)
                feedListings.value = posts.map { it.toListing() }
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Success,
                        page = 1,
                        endReached = posts.isEmpty(),
                        isDefaultFeed = true,
                        bottomLoading = false
                    )
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching newly listed posts: ", e)
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
            val result = fetchNextScrollPage(state.page, feedListings.value) { nextPage ->
                if (state.isDefaultFeed) {
                    resellPostRepository.getNewlyListedPosts(page = nextPage, limit = HOME_PAGE_SIZE)
                } else {
                    resellPostRepository.getFilteredPosts(state.activeFilter, page = nextPage, limit = HOME_PAGE_SIZE)
                }
            }
            when (result) {
                is ScrollPageResult.Success -> {
                    feedListings.value += result.newListings
                    applyMutation {
                        copy(
                            page = result.nextPage,
                            endReached = result.endReached,
                            bottomLoading = false
                        )
                    }
                }

                is ScrollPageResult.Error -> {
                    Log.e(TAG, "Error fetching next page: ", result.exception)
                    applyMutation { copy(bottomLoading = false) }
                }
            }
        }
    }

    fun onFilterChanged(filter: ResellFilter) {
        applyMutation {
            copy(
                activeFilter = filter,
                loadedState = ResellApiState.Loading,
                bottomLoading = false
            )
        }
        getPosts(filter)
    }


    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }

    fun onNotifPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.NOTIFS)
    }

}
