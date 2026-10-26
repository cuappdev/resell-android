package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val HOME_PAGE_SIZE = 10

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    private val coilRepository: CoilRepository
) :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = emptyList(),
            savedListings = emptyList(),
            fromSearchListings = emptyList(),
            fromPurchasesListings = emptyList(),
            activeFilter = ResellFilter(sortBy = ResellFilter.SortBy.NEWLY_LISTED),
            loadedState = ResellApiState.Loading,
            savedImageResponses = emptyList(),
            searchedImageResponses = emptyList(),
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
        val listings: List<Listing>,
        val savedListings: List<Listing>,
        val fromSearchListings: List<Listing>,
        val fromPurchasesListings: List<Listing>,
        val activeFilter: ResellFilter,
        val page: Int,
        val bottomLoading: Boolean,
        val endReached: Boolean,
        val isDefaultFeed: Boolean,
        val savedImageResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>,
        val searchedImageResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>
    )

    private var fetchJob: Job? = null

    init {
        getNewlyListedPosts()
        resellPostRepository.fetchSavedPosts()
        resellPostRepository.getSearchHistory()
        asyncCollect(resellPostRepository.savedPosts) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    savedListings = response.asSuccessOrNull()?.data?.map { it.toListing() }
                        ?: emptyList(),
                    savedImageResponses = when (response) {
                        is ResellApiResponse.Success -> {
                            response.data.map { listing ->
                                coilRepository.getUrlState(listing.images.firstOrNull() ?: "")
                            }
                        }

                        else -> {
                            emptyList()
                        }
                    }
                )
            }
        }

        asyncCollect(resellPostRepository.fromSearchedPosts) { response ->
            CoroutineScope(Dispatchers.Default).launch {
                val fromSearchListings = response.asSuccessOrNull()?.data
                    ?.flatMap { it.second }
                    ?.take(4)
                    ?: emptyList()

                val searchImageResponses = fromSearchListings.map { listing ->
                    coilRepository.getUrlState(listing.images.firstOrNull() ?: "")
                }

                applyMutation {
                    copy(
                        fromSearchListings = fromSearchListings,
                        searchedImageResponses = searchImageResponses
                    )
                }
            }

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
                val orderedPosts = if (filter.sortBy == ResellFilter.SortBy.NEWLY_LISTED) {
                    posts.sortedByDescending { it.createdDate }
                } else {
                    posts
                }
                applyMutation {
                    copy(
                        listings = orderedPosts.map { it.toListing() },
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
                applyMutation {
                    copy(
                        listings = posts.map { it.toListing() },
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
            try {
                val nextPage = state.page + 1
                val posts = if (state.isDefaultFeed) {
                    resellPostRepository.getNewlyListedPosts(page = nextPage, limit = HOME_PAGE_SIZE)
                } else {
                    resellPostRepository.getFilteredPosts(state.activeFilter, page = nextPage, limit = HOME_PAGE_SIZE)
                }
                val orderedPosts =
                    if (!state.isDefaultFeed && state.activeFilter.sortBy == ResellFilter.SortBy.NEWLY_LISTED) {
                        posts.sortedByDescending { it.createdDate }
                    } else {
                        posts
                    }
                applyMutation {
                    // Prevent duplicates from rendering
                    val existingIds = listings.map { it.id }.toSet()
                    val newListings = orderedPosts.map { it.toListing() }
                        .filterNot { it.id in existingIds }
                    copy(
                        listings = listings + newListings,
                        page = nextPage,
                        endReached = newListings.isEmpty(),
                        bottomLoading = false
                    )
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching next page: ", e)
                applyMutation { copy(bottomLoading = false) }
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
