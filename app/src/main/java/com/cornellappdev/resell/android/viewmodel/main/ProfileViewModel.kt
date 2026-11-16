package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ImageStateObserver
import com.cornellappdev.resell.android.viewmodel.observeListingResponse
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val profileRepository: ProfileRepository,
    private val userInfoRepository: UserInfoRepository,
    coilRepository: CoilRepository,
): ResellViewModel<ProfileViewModel.ProfileUiState>(
    initialUiState = ProfileUiState(
        profileTab = ProfileTab.SHOP,
        loadedState = ResellApiState.Loading,
        shopListings = ResellApiResponse.Pending,
        archiveListings = ResellApiResponse.Pending,
        shopName = "",
        vendorName = "",
        bio = "",
        imageUrl = "",
    )
) {

    data class ProfileUiState(
        val profileTab: ProfileTab,
        val loadedState: ResellApiState,
        val shopListings: ResellApiResponse<List<ListingUi>>,
        val archiveListings: ResellApiResponse<List<ListingUi>>,
        val shopName: String,
        val vendorName: String,
        val bio: String,
        val imageUrl: String,
    )

    enum class ProfileTab {
        // Please preserve order!
        SHOP, ARCHIVE
    }

    private val shopObserver =
        ImageStateObserver<ResellApiResponse<List<ListingUi>>>(coilRepository, viewModelScope) { listings ->
            applyMutation { copy(shopListings = listings) }
        }

    private val archiveObserver =
        ImageStateObserver<ResellApiResponse<List<ListingUi>>>(coilRepository, viewModelScope) { listings ->
            applyMutation { copy(archiveListings = listings) }
        }

    fun onTabSelected(profileTab: ProfileTab) {
        applyMutation {
            copy(
                profileTab = profileTab,
            )
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

    fun onSettingsPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.SETTINGS)
    }
    fun onCalendarPressed() {
        rootNavigationRepository.navigate(ResellRootRoute.AVAILABILITY)
    }

    /**
     * Reloads the listings made by the internal user.
     */
    fun onReloadListings() {
        shopObserver.stopWith(ResellApiResponse.Pending)
        archiveObserver.stopWith(ResellApiResponse.Pending)
        viewModelScope.launch {
            profileRepository.fetchInternalListings(userInfoRepository.getUserId() ?: "lol")
            profileRepository.fetchArchivedListings(userInfoRepository.getUserId() ?: "lol")
        }
    }

    init {
        // Fetch user info on first.
        viewModelScope.launch {
            profileRepository.fetchInternalProfile(userInfoRepository.getUserId() ?: "lol")
            onReloadListings()
        }

        asyncCollect(profileRepository.internalUser) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    shopName = response.asSuccessOrNull()?.data?.username ?: "",
                    vendorName = response.asSuccessOrNull()?.data?.name ?: "",
                    bio = response.asSuccessOrNull()?.data?.bio ?: "",
                    imageUrl = response.asSuccessOrNull()?.data?.imageUrl ?: "",
                )
            }
        }

        asyncCollect(profileRepository.internalListings) { response ->
            shopObserver.observeListingResponse(response)
        }

        asyncCollect(profileRepository.internalArchivedListings) { response ->
            archiveObserver.observeListingResponse(response)
        }
    }
}
