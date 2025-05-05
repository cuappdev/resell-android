package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
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
class HomeViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
    private val coilRepository: CoilRepository
) :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = emptyList(),
            savedListings = emptyList(),
            activeFilter = HomeFilter.RECENT,
            loadedState = ResellApiState.Loading,
            savedImageResponses = emptyList(),
            page = 1,
            bottomLoading = false
        )
    ) {

    data class HomeUiState(
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val savedListings: List<Listing>,
        val activeFilter: HomeFilter,
        val page: Int,
        val bottomLoading: Boolean,
        val savedImageResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>
    ) {
        // TODO This should change to an endpoint, but backend is simple.
        val filteredListings: List<Listing>
            get() = listings.filter { listing ->
                activeFilter == HomeFilter.RECENT ||
                        listing.categories.map { it.lowercase() }.any {
                            it.contains(activeFilter.name.lowercase())
                        }
            }
    }

    init {
        onRecentPressed()
        resellPostRepository.fetchSavedPosts()
        asyncCollect(resellPostRepository.savedPosts) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    savedListings = response.asSuccessOrNull()?.data?.map { it.toListing() }
                        ?: emptyList(),
                    savedImageResponses = when(response) {
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

    }

    enum class HomeFilter {
        RECENT, CLOTHING, BOOKS, SCHOOL, ELECTRONICS, HOUSEHOLD, HANDMADE, SPORTS, OTHER
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

    private fun onRecentPressed() {
        applyMutation {
            copy(
                activeFilter = HomeFilter.RECENT,
                page = 1,
                loadedState = ResellApiState.Loading
            )
        }
        viewModelScope.launch {
            try {
                val posts = resellPostRepository.getPostsByPage(1)
                applyMutation {
                    copy(
                        listings = posts.map { it.toListing() },
                        loadedState = ResellApiState.Success
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching posts: ", e)
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Error
                    )
                }
            }
        }
    }

    fun onToggleFilter(filter: HomeFilter) {
        if (filter == HomeFilter.RECENT) {
            onRecentPressed()
            return
        }

        applyMutation {
            copy(
                activeFilter = filter,
                loadedState = ResellApiState.Loading,
            )
        }
        viewModelScope.launch {
            try {
                val posts = resellPostRepository.getPostsByFilter(
                    filter.name
                )
                applyMutation {
                    copy(
                        listings = posts.map { it.toListing() },
                        loadedState = ResellApiState.Success
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching posts: ", e)
                applyMutation {
                    copy(
                        loadedState = ResellApiState.Error
                    )
                }
            }
        }
    }

    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }

    fun onHitBottom() {
        if (stateValue().bottomLoading || stateValue().activeFilter != HomeFilter.RECENT) {
            return
        }

        viewModelScope.launch {
            applyMutation {
                copy(
                    page = page + 1,
                    bottomLoading = true
                )
            }

            val newPage = resellPostRepository.getPostsByPage(stateValue().page).map {
                it.toListing()
            }
            applyMutation {
                copy(
                    listings = stateValue().listings + newPage,
                    bottomLoading = false
                )
            }
        }
    }
}
