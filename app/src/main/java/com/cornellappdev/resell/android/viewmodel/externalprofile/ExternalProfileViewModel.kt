package com.cornellappdev.resell.android.viewmodel.externalprofile

import androidx.compose.ui.Alignment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.screens.externalprofile.ExternalProfileRoute
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
class ExternalProfileViewModel @Inject constructor(
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val blockedUsersRepository: BlockedUsersRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val profileRepository: ProfileRepository,
    private val externalNavigationRepository: ExternalNavigationRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<ExternalProfileViewModel.ExternalProfileState>(
        initialUiState = ExternalProfileState(
            shopName = "",
            vendorName = "",
            bio = "",
            imageUrl = "",
            loadedState = ResellApiState.Loading,
            shopListings = ResellApiResponse.Pending,
            archiveListings = ResellApiResponse.Pending,
            uid = ""
        )
    ) {

    data class ExternalProfileState(
        val isLoading: Boolean = false,
        val shopName: String,
        val vendorName: String,
        val bio: String,
        val imageUrl: String,
        val loadedState: ResellApiState,
        val shopListings: ResellApiResponse<List<Listing>>,
        val archiveListings: ResellApiResponse<List<Listing>>,
        val uid: String
    )

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

    fun onEllipsisPressed() {
        rootOptionsMenuRepository.showOptionsMenu(
            options = listOf(
                OptionType.SHARE,
                OptionType.REPORT,
                OptionType.BLOCK,
            ),
            alignment = Alignment.TopStart,
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

    fun onSearchPressed() {
        externalNavigationRepository.navigate(
            ExternalProfileRoute.Search(
                uid = stateValue().uid,
                username = stateValue().shopName
            )
        )
    }

    /**
     * Reloads the listings made by the external user.
     */
    fun onReloadListings(id: String) {
        viewModelScope.launch {
            profileRepository.fetchExternalListings(id)
        }
    }

    init {
        val navArgs = savedStateHandle.toRoute<ExternalProfileRoute.ExternalProfile>()
        applyMutation {
            copy(
                uid = navArgs.uid,
            )
        }

        // Fetch user info on first.
        viewModelScope.launch {
            profileRepository.fetchExternalProfile(navArgs.uid)
            onReloadListings(navArgs.uid)
        }

        asyncCollect(profileRepository.externalUser) { response ->
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

        asyncCollect(profileRepository.externalListings) { response ->
            applyMutation {
                copy(
                    loadedState = response.toResellApiState(),
                    shopListings = response
                )
            }
        }
    }
}
