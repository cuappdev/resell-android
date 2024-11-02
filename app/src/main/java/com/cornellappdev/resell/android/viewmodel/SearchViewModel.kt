package com.cornellappdev.resell.android.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.profile.SearchRepository
import com.cornellappdev.resell.android.ui.screens.externalprofile.ExternalProfileRoute
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalNavigationRepository
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class SearchViewModel(
    private val searchRepository: SearchRepository,
) : ResellViewModel<
        SearchViewModel.SearchUiState>(
    initialUiState = SearchUiState(
        query = "",
        listings = ResellApiResponse.Pending,
        uid = null,
        username = ""
    )
) {

    data class SearchUiState(
        val query: String,
        val listings: ResellApiResponse<List<Listing>>,
        val uid: String?,
        val username: String
    ) {
        val placeholderText
            get() = "Search ${username}..."
    }

    fun onQueryChanged(query: String) {
        applyMutation { copy(query = query) }

        // Try to fetch listings from backend
        viewModelScope.launch {
            applyMutation {
                copy(
                    listings = ResellApiResponse.Pending
                )
            }
            try {
                val response = searchRepository.searchPostByUser(stateValue().uid, query)
                // By this time, the query may have changed. If so, ignore.
                if (stateValue().query == query) {
                    applyMutation { copy(listings = ResellApiResponse.Success(response)) }
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", e.toString())
                applyMutation { copy(listings = ResellApiResponse.Error) }
            }
        }
    }

    fun onListingPressed(listing: Listing) {

    }

    abstract fun onExit()
}

@HiltViewModel
class ExternalProfileSearchViewModel @Inject constructor(
    private val externalNavigationRepository: ExternalNavigationRepository,
    searchRepository: SearchRepository,
    savedStateHandle: SavedStateHandle,
) : SearchViewModel(searchRepository) {
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
) : SearchViewModel(searchRepository) {
    init {
        applyMutation { copy(uid = null, username = "") }
    }

    override fun onExit() {
        rootNavigationRepository.popBackStack()
    }
}
