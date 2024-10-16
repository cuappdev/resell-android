package com.cornellappdev.resell.android.viewmodel.settings

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val blockedUsersRepository: BlockedUsersRepository,
    private val dialogRepository: RootDialogRepository,
    private val confirmationRepository: RootConfirmationRepository,
) : ResellViewModel<BlockedUsersViewModel.BlockedUsersUiState>(
    initialUiState = BlockedUsersUiState(),
) {

    data class BlockedUsersUiState(
        val blockedUsers: List<UiBlockedUser> = emptyList(),
    )

    data class UiBlockedUser(
        val id: String,
        val name: String,
        val imageUrl: String,
    )

    fun onUnblock(id: String) {
        val name = stateValue().blockedUsers.firstOrNull { it.id == id }?.name ?: "User"
        dialogRepository.showDialog(
            RootDialogContent.TwoButtonDialog(
                title = "Unblock $name?",
                description = "They will be able to message you and view your posts.",
                primaryButtonText = "Unblock",
                secondaryButtonText = "Cancel",
                onPrimaryButtonClick = {
                    viewModelScope.launch {
                        dialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                        blockedUsersRepository.onUnblockUser(
                            userId = id,
                            onError = {
                                // TODO
                                dialogRepository.dismissDialog()
                                confirmationRepository.showError()
                            },
                            onSuccess = {
                                // TODO
                                dialogRepository.dismissDialog()
                                confirmationRepository.showSuccess(
                                    message = "$name has been unblocked.",
                                )
                            }
                        )
                    }
                },
                onSecondaryButtonClick = {
                    dialogRepository.dismissDialog()
                },
                exitButton = true,
                primaryButtonContainer = ResellTextButtonContainer.SECONDARY_RED
            )
        )
    }

    init {
        blockedUsersRepository.fetchBlockedUsers { blockedUsers ->
            applyMutation { copy(blockedUsers = blockedUsers) }
        }
    }
}
