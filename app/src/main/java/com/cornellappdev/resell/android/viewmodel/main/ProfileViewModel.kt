package com.cornellappdev.resell.android.viewmodel.main

import androidx.compose.ui.Alignment
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.OptionType
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val blockedUsersRepository: BlockedUsersRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val profileRepository: ProfileRepository,
    private val userInfoRepository: UserInfoRepository
) : ResellViewModel<ProfileViewModel.ProfileUiState>(
    initialUiState = ProfileUiState(
        profileTab = ProfileTab.SHOP,
        loadedState = ResellApiState.Loading,
        shopListings = emptyList(),
        archiveListings = emptyList(),
        shopName = "Sunshine Shop",
        vendorName = "Richie Sun",
        bio = "I cook food and you eat it. Simple.\nAlsotest\n\n\nthis\n\n\ngogogo",
        imageUrl = "",
        isSelf = false,
        postsLoadedState = ResellApiState.Loading
    )
) {

    data class ProfileUiState(
        val profileTab: ProfileTab,
        val loadedState: ResellApiState,
        val postsLoadedState: ResellApiState,
        val shopListings: List<Listing>,
        val archiveListings: List<Listing>,
        val shopName: String,
        val vendorName: String,
        val bio: String,
        val imageUrl: String,
        val isSelf: Boolean,
    )

    enum class ProfileTab {
        // Please preserve order!
        SHOP, ARCHIVE, WISHLIST
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

    fun onSettingsPressed() {
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

    /**
     * Reloads the listings made by the internal user.
     */
    fun onReloadListings() {
        applyMutation {
            copy(
                postsLoadedState = ResellApiState.Loading
            )
        }

        viewModelScope.launch {
            profileRepository.fetchInternalListings(userInfoRepository.getUserId() ?: "lol")
            profileRepository.fetchArchivedListings(userInfoRepository.getUserId() ?: "lol")

            applyMutation {
                copy(
                    postsLoadedState = ResellApiState.Success
                )
            }
        }
    }

    init {
        // Fetch user info on first.
        viewModelScope.launch {
            profileRepository.fetchInternalProfile(userInfoRepository.getUserId() ?: "lol")
            onReloadListings()
        }

        // TODO: Check if this is the internal profile... or separately implement external.
        asyncCollect(profileRepository.internalUser) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    shopName = response.asSuccessOrNull()?.data?.username ?: "",
                    vendorName = response.asSuccessOrNull()?.data?.name ?: "",
                    bio = response.asSuccessOrNull()?.data?.bio ?: "",
                    imageUrl = response.asSuccessOrNull()?.data?.imageUrl ?: "",
                    isSelf = true,
                )
            }
        }

        asyncCollect(profileRepository.internalListings) { response ->
            applyMutation {
                copy(
                    shopListings = response.asSuccessOrNull()?.data ?: listOf(),
                )
            }
        }

        asyncCollect(profileRepository.internalArchivedListings) { response ->
            applyMutation {
                copy(
                    archiveListings = response.asSuccessOrNull()?.data ?: listOf(),
                )
            }
        }
    }
}
