package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestMatchesViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ResellViewModel<
        RequestMatchesViewModel.RequestMatchesUiState>(
    initialUiState = RequestMatchesUiState(ResellApiResponse.Pending, "", "")
) {

    data class RequestMatchesUiState(
        val listings: ResellApiResponse<List<Listing>>,
        val id: String,
        private val requestTitle: String
    ) {
        val title
            get() = "$requestTitle\nRequest Matches"
    }

    fun onExit() {
        rootNavigationRepository.popBackStack()
    }

    fun onListingPressed(listing: Listing) {
        rootNavigationRepository.navigateToPdp(listing)
    }

    fun onLoadMatches() {
        viewModelScope.launch {
            try {
                applyMutation {
                    copy(
                        listings = ResellApiResponse.Pending
                    )
                }
                val response = profileRepository.getRequestById(stateValue().id)
                applyMutation {
                    copy(
                        listings = ResellApiResponse.Success(response.request.toRequestListing().matches)
                    )
                }
            }
            catch (e: Exception) {
                Log.e("RequestMatchesViewModel", "Error fetching matches", e)
                applyMutation {
                    copy(
                        listings = ResellApiResponse.Error
                    )
                }
            }
        }
    }

    init {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.REQUEST_MATCHES>()
        applyMutation {
            copy(
                requestTitle = navArgs.title,
                id = navArgs.id
            )
        }
        onLoadMatches()
    }
}
