package com.cornellappdev.resell.android.viewmodel.report

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportConfirmationViewModel @Inject constructor(
    private val rootDialogRepository: RootDialogRepository,
    private val rootConfirmRepository: RootConfirmationRepository,
    private val blockedUsersRepository: BlockedUsersRepository,
    private val rootNavigationRepository: RootNavigationRepository,
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
        private val userId: String,
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
            get() = "Block Account?"

        val blockButton: String
            get() = "Block $userId"
    }

    fun onBlockPressed() {
        showBlockDialog(
            rootDialogRepository = rootDialogRepository,
            rootConfirmationRepository = rootConfirmRepository,
            blockedUsersRepository = blockedUsersRepository,
            onBlockSuccess = {
                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
            }
        )
    }

    fun onDonePressed() {
        rootNavigationRepository.navigate(ResellRootRoute.MAIN)
    }

    init {
        val navArgs = savedStateHandle.toRoute<ReportScreen.Confirmation>()

        applyMutation {
            copy(
                reportPost = navArgs.reportPost,
            )
        }
    }
}
