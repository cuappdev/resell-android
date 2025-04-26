package com.cornellappdev.resell.android.viewmodel.externalprofile

import androidx.compose.ui.Alignment
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
            uid = "",
            blockedUsers = listOf()
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
        val uid: String,
        private val blockedUsers: List<String>,
    ) {
        val isBlocked: Boolean
            get() = blockedUsers.contains(uid)
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

    fun onEllipsisPressed() {
        rootOptionsMenuRepository.showOptionsMenu(
            options = listOf(
                OptionType.SHARE,
                OptionType.REPORT,
                if (stateValue().isBlocked) OptionType.UNBLOCK else OptionType.BLOCK,
            ),
            alignment = Alignment.TopStart,
        ) {
            when (it) {
                OptionType.SHARE -> {
                    // TODO: Implement
                }

                OptionType.REPORT -> {
                    rootNavigationRepository.navigate(
                        ResellRootRoute.REPORT(
                            reportPost = false,
                            postId = "",
                            userId = stateValue().uid,
                        )
                    )
                }

                OptionType.BLOCK -> {
                    showBlockDialog(
                        rootDialogRepository = rootDialogRepository,
                        blockedUsersRepository = blockedUsersRepository,
                        rootConfirmationRepository = rootConfirmationRepository,
                        userId = stateValue().uid,
                        onBlockSuccess = {
                            rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                        }
                    )
                }

                OptionType.UNBLOCK -> {
                    showUnblockDialog(
                        dialogRepository = rootDialogRepository,
                        blockedUsersRepository = blockedUsersRepository,
                        rootConfirmationRepository = rootConfirmationRepository,
                        userId = stateValue().uid,
                        name = stateValue().vendorName
                    )
                }

                else -> {}
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
        profileRepository.fetchExternalListings(id)
    }

    init {
        val navArgs = savedStateHandle.toRoute<ExternalProfileRoute.ExternalProfile>()
        blockedUsersRepository.fetchBlockedUsers()
        applyMutation {
            copy(
                uid = navArgs.uid,
            )
        }

        // Fetch user info on first.
        profileRepository.fetchExternalProfile(navArgs.uid)
        onReloadListings(navArgs.uid)

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

        asyncCollect(blockedUsersRepository.blockedUsers) { response ->
            response.ifSuccess {
                applyMutation {
                    copy(
                        blockedUsers = it.map { it.id }
                    )
                }
            }
        }
    }
}
