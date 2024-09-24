package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.Listing
import com.cornellappdev.resell.android.model.LoginRepository
import com.cornellappdev.resell.android.model.ResellApiState
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.richieListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val navController: NavHostController,
) : ResellViewModel<ProfileViewModel.ProfileUiState>(
    initialUiState = ProfileUiState(
        profileTab = ProfileTab.SHOP,
        loadedState = ResellApiState.Success,
        listings = emptyList(),
        shopName = "Sunshine Shop",
        vendorName = "Richie Sun",
        bio = "I cook food and you eat it. Simple.",
        imageUrl = "https://media.licdn.com/dms/image/v2/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1704329714345?e=1732752000&v=beta&t=XQ8dS9-QbteQY060_x6J5XVNpUy7YUJ1SRLE2oaYcaM",
    )
) {

    data class ProfileUiState(
        val profileTab: ProfileTab,
        val loadedState: ResellApiState,
        val listings: List<Listing>,
        val shopName: String,
        val vendorName: String,
        val bio: String,
        val imageUrl: String,
    )

    enum class ProfileTab {
        // Please preserve order!
        SHOP, ARCHIVE, WISHLIST
    }

    fun onTabSelected(profileTab: ProfileTab) {
        applyMutation {
            copy(
                profileTab = profileTab,
                loadedState = ResellApiState.Loading
            )
        }
        viewModelScope.launch {
            // TODO: Mock request
            delay(2000)
            applyMutation {
                copy(
                    listings = richieListings(20),
                    loadedState = ResellApiState.Success
                )
            }
        }
    }

    fun onSignOutClick() {
        // TODO: Implement
        loginRepository.invalidateEmail()
        navController.navigate(ResellRootRoute.LOGIN)
    }
}
