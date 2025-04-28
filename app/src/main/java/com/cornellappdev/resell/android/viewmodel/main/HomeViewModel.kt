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
            activeFilter = ResellFilter(),
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
        val activeFilter: ResellFilter,
        val page: Int,
        val bottomLoading: Boolean,
        val savedImageResponses: List<MutableState<ResellApiResponse<ImageBitmap>>>
    ) {
    }

    init {
        getPosts(ResellFilter())
        resellPostRepository.fetchSavedPosts()
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

    }

    // todo delete
    enum class HomeFilter {
        RECENT, CLOTHING, BOOKS, SCHOOL, ELECTRONICS, HOUSEHOLD, HANDMADE, SPORTS, OTHER
    }

    data class ResellFilter(
        val priceRange: IntRange = 0..1000,
        val itemsOnSale: Boolean = false,
        val categoriesSelected: List<Category> = emptyList(),
        val conditionSelected: Condition? = null,
        val sortBy: SortBy = SortBy.ANY
    )

    enum class SortBy(val label: String) {
        ANY("Any"),
        NEWLY_LISTED("Newly Listed"),
        PRICE_HIGH_TO_LOW("Price: High to Low"),
        PRICE_LOW_TO_HIGH("Price: Low to High")
    }

    enum class Condition(val label: String) {
        GENTLY_USED("Gently Used"),
        WORN("Worn"),
        NEVER_USED("Never Used")
    }

    enum class Category(val label: String) {
        CLOTHING("Clothing"),
        BOOKS("Books"),
        SCHOOL("School"),
        ELECTRONICS("Electronics"),
        HANDMADE("Handmade"),
        SPORTS("Sports & Outdoors"),
        OTHER("Other")
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
        viewModelScope.launch {
            try {
//                val posts = resellPostRepository.getPostsByPage(1)
                val posts = resellPostRepository.getFilteredPosts(filter)
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

    fun onFilterChanged(filter: ResellFilter) {
        applyMutation {
            copy(
                activeFilter = filter,
                loadedState = ResellApiState.Loading,
            )
        }
        getPosts(filter)
    }


    fun onSearchPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SEARCH)
    }
}
