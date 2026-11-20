package com.cornellappdev.resell.android.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.domain.GetListingUisUseCase
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.profile.SearchRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.ui.screens.externalprofile.ExternalProfileRoute
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalNavigationRepository
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

abstract class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    getListingUis: GetListingUisUseCase,
) : ResellViewModel<
        SearchViewModel.SearchUiState>(
    initialUiState = SearchUiState(
        query = "",
        listings = ResellApiResponse.Success(emptyList()),
        uid = null,
        username = ""
    )
) {

    data class SearchUiState(
        val query: String,
        val listings: ResellApiResponse<List<ListingUi>>,
        val uid: String?,
        val username: String,
        val focusKeyboard: UIEvent<Unit>? = null,
    ) {
        val placeholderText
            get() = "Search ${username}..."
    }

    private val results =
        MutableStateFlow<ResellApiResponse<List<Listing>>>(ResellApiResponse.Success(emptyList()))

    init {
        asyncCollect(results.flatMapLatest { getListingUis(it) }) { listings ->
            applyMutation { copy(listings = listings) }
        }
    }

    fun onQueryChanged(query: String) {
        applyMutation { copy(query = query) }
        if (query.isEmpty() || query.isBlank()) {
            results.value = ResellApiResponse.Success(emptyList())
            return
        }
        // Try to fetch listings from backend
        viewModelScope.launch {
            results.value = ResellApiResponse.Pending
            try {
                val response = searchRepository.searchPostByUser(stateValue().uid, query)
                // By this time, the query may have changed. If so, ignore.
                if (stateValue().query == query) {
                    results.value = ResellApiResponse.Success(response)
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", e.toString())
                results.value = ResellApiResponse.Error
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

    abstract fun onExit()

    init {
        viewModelScope.launch {
            delay(350L.milliseconds)
            applyMutation {
                copy(
                    focusKeyboard = UIEvent(Unit)
                )
            }
        }
    }
}

@HiltViewModel
class ExternalProfileSearchViewModel @Inject constructor(
    private val externalNavigationRepository: ExternalNavigationRepository,
    searchRepository: SearchRepository,
    savedStateHandle: SavedStateHandle,
    rootNavigationRepository: RootNavigationRepository,
    getListingUis: GetListingUisUseCase,
) : SearchViewModel(searchRepository, rootNavigationRepository, getListingUis) {
    init {
        val navArgs = savedStateHandle.toRoute<ExternalProfileRoute.Search>()

        applyMutation { copy(uid = navArgs.uid, username = navArgs.username) }
    }

    override fun onExit() {
        externalNavigationRepository.popBackStack()
    }
}

@HiltViewModel
class AllSearchViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    searchRepository: SearchRepository,
    getListingUis: GetListingUisUseCase,
) : SearchViewModel(searchRepository, rootNavigationRepository, getListingUis) {
    init {
        applyMutation { copy(uid = null, username = "") }
    }

    override fun onExit() {
        rootNavigationRepository.popBackStack()
    }
}
