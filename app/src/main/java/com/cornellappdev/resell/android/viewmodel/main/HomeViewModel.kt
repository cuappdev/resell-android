package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val resellPostRepository: ResellPostRepository,
) :
    ResellViewModel<HomeViewModel.HomeUiState>(
        initialUiState = HomeUiState(
            listings = listOf(),
            activeFilter = HomeFilter.RECENT,
            loadedState = ResellApiState.Loading
        )
    ) {

    data class HomeUiState(
        val loadedState: ResellApiState,
        private val listings: List<Listing>,
        val activeFilter: HomeFilter,
    ) {
        // TODO This should change to an endpoint, but backend is simple.
        val filteredListings: List<Listing>
            get() = listings.filter {
                activeFilter == HomeFilter.RECENT ||
                        it.categories.map { it.lowercase() }.any {
                            it.contains(activeFilter.name.lowercase())
                        }
            }
    }

    init {
        asyncCollect(resellPostRepository.allPostsFlow) { response ->
            val posts = when (response) {
                is ResellApiResponse.Success -> {
                    response.data
                }

                else -> {
                    listOf()
                }
            }

            applyMutation {
                copy(
                    listings = posts.map { it.toListing() },
                    loadedState = response.toResellApiState()
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
                id = listing.id,
                title = listing.title,
                price = listing.price,
                images = listing.images,
                description = listing.description,
                categories = listing.categories,
                userImageUrl = listing.user.imageUrl,
                username = listing.user.username,
                userId = listing.user.id,
                userHumanName = listing.user.name
            )
        )
    }

    fun onToggleFilter(filter: HomeFilter) {
        applyMutation {
            copy(activeFilter = filter)
        }
    }

    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }
}