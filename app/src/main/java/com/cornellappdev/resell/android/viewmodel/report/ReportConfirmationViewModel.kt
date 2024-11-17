package com.cornellappdev.resell.android.viewmodel.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportConfirmationViewModel @Inject constructor(
    private val rootDialogRepository: RootDialogRepository,
    private val rootConfirmRepository: RootConfirmationRepository,
    private val blockedUsersRepository: BlockedUsersRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<ReportConfirmationViewModel.ConfirmationUiState>(
        initialUiState = ConfirmationUiState(
            reportPost = true,
            userId = ""
        )
    ) {


    data class ConfirmationUiState(
        private val reportPost: Boolean,
        val userId: String,
        val accountName: ResellApiResponse<String> = ResellApiResponse.Pending
    ) {
        val headerTitle: String
            get() = if (reportPost) {
                "Report Post"
            } else {
                "Report Account"
            }

        val title: String
            get() = if (reportPost) {
                "Thank you for reporting this post"
            } else {
                "Thank you for reporting this account"
            }

        val body: String
            get() = "Your report is valued in keeping Resell a safe community. We will be carefully reviewing the ${if (reportPost) "post" else "account"} and taking any necessary action."

        val blockText: String
            get() = if (accountName is ResellApiResponse.Success) "Block ${accountName.data}?" else "Block Account?"

        val blockButton: String
            get() = "Block"
    }

    fun onBlockPressed() {
        showBlockDialog(
            rootDialogRepository = rootDialogRepository,
            rootConfirmationRepository = rootConfirmRepository,
            blockedUsersRepository = blockedUsersRepository,
            onBlockSuccess = {
                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
            },
            userId = stateValue().userId
        )
    }

    fun onDonePressed() {
        rootNavigationRepository.navigate(ResellRootRoute.MAIN)
    }

    init {
        val navArgs = savedStateHandle.toRoute<ReportScreen.Confirmation>()

        viewModelScope.launch {
            try {
                val user = profileRepository.getUserById(navArgs.userId)
                applyMutation {
                    copy(
                        accountName = ResellApiResponse.Success(user.user.toUserInfo().name)
                    )
                }
            }
            catch (e: Exception) {
                applyMutation {
                    copy(
                        accountName = ResellApiResponse.Error
                    )
                }
            }
        }

        applyMutation {
            copy(
                reportPost = navArgs.reportPost,
                userId = navArgs.userId
            )
        }
    }
}
