package com.cornellappdev.resell.android.viewmodel.main

import androidx.compose.ui.Alignment
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.Listing
import com.cornellappdev.resell.android.model.LoginRepository
import com.cornellappdev.resell.android.model.ResellApiState
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.richieListings
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.OptionType
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val blockedUsersRepository: BlockedUsersRepository,
    private val rootConfirmationRepository: RootConfirmationRepository
) : ResellViewModel<ProfileViewModel.ProfileUiState>(
    initialUiState = ProfileUiState(
        profileTab = ProfileTab.SHOP,
        loadedState = ResellApiState.Success,
        shopListings = emptyList(),
        archiveListings = emptyList(),
        shopName = "Sunshine Shop",
        vendorName = "Richie Sun",
        bio = "I cook food and you eat it. Simple.\nAlsotest\n\n\nthis\n\n\ngogogo",
        imageUrl = "https://media.licdn.com/dms/image/v2/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1704329714345?e=1732752000&v=beta&t=XQ8dS9-QbteQY060_x6J5XVNpUy7YUJ1SRLE2oaYcaM",
    )
) {

    data class ProfileUiState(
        val profileTab: ProfileTab,
        val loadedState: ResellApiState,
        val shopListings: List<Listing>,
        val archiveListings: List<Listing>,
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
                loadedState = ResellApiState.Loading,
                shopListings = listOf(),
            )
        }
        viewModelScope.launch {
            // TODO: Mock request
            delay(2000)
            applyMutation {
                when (profileTab) {
                    ProfileTab.SHOP -> {
                        copy(
                            shopListings = richieListings(20),
                            loadedState = ResellApiState.Success
                        )
                    }

                    ProfileTab.ARCHIVE -> {
                        copy(
                            archiveListings = richieListings(20),
                            loadedState = ResellApiState.Success
                        )
                    }

                    else -> {
                        copy()
                    }
                }
            }
        }
    }

    fun onListingPressed(listing: Listing) {
        // TODO: Implement
    }

    fun onSignOutClick() {
        // TODO: Implement
        loginRepository.invalidateEmail()
        rootNavigationRepository.navigate(ResellRootRoute.LANDING)
    }

    fun onSettingsPressed() {
        // TODO: Implement
        rootNavigationRepository.navigate(ResellRootRoute.SETTINGS)
    }

    fun onSearchPressed() {
        // TODO: Implement

        // TODO: showing this for testing
        rootOptionsMenuRepository.showOptionsMenu(
            options = listOf(
                OptionType.SHARE,
                OptionType.REPORT,
                OptionType.BLOCK,
            ),
            alignment = Alignment.TopEnd,
        ) {
            when (it) {
                OptionType.SHARE -> {
                    // TODO: Implement
                }

                OptionType.REPORT -> {
                    // TODO: user id and post id
                    rootNavigationRepository.navigate(
                        ResellRootRoute.REPORT(
                            reportPost = false,
                            postId = "",
                            userId = "",
                        )
                    )
                }

                OptionType.BLOCK -> {
                    showBlockDialog(
                        rootDialogRepository = rootDialogRepository,
                        blockedUsersRepository = blockedUsersRepository,
                        rootConfirmationRepository = rootConfirmationRepository
                    )
                }
            }
        }
    }
}
