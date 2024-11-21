package com.cornellappdev.resell.android.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class ResellViewModel<UiState>(initialUiState: UiState) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(initialUiState)
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    @Composable
    fun collectUiStateValue(): UiState = uiStateFlow.collectAsState().value

    /**
     * Applies a mutation to the current [UiState] and emits the new state.
     *
     * @param mutation A function that operates on the current [UiState] and returns a new [UiState].
     *
     * Most often, you'll want to `copy` the current state, changing just one of its properties,
     * and then emit the new state.
     */
    fun applyMutation(mutation: UiState.() -> UiState) {
        _uiStateFlow.value = _uiStateFlow.value.mutation()
    }

    /**
     * Asynchronously starts collecting the given flow.
     */
    fun <T> asyncCollect(flow: StateFlow<T>, collector: (T) -> Unit): Job {
        return viewModelScope.launch {
            flow.collect { collector(it) }
        }
    }

    /**
     * Returns the current [UiState].
     */
    protected fun stateValue(): UiState {
        return _uiStateFlow.value
    }

    protected fun showBlockDialog(
        rootDialogRepository: RootDialogRepository,
        rootConfirmationRepository: RootConfirmationRepository,
        blockedUsersRepository: BlockedUsersRepository,
        onBlockSuccess: () -> Unit = {},
        onBlockError: () -> Unit = {},
        userId: String
    ) {
        rootDialogRepository.showDialog(
            RootDialogContent.TwoButtonDialog(
                title = "Block User",
                description = "Are you sure you’d like to block this user?",
                primaryButtonText = "Block",
                onPrimaryButtonClick = {
                    rootDialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                    blockedUsersRepository.onBlockUser(
                        userId = userId,
                        onError = {
                            rootDialogRepository.dismissDialog()
                            rootConfirmationRepository.showError()
                            onBlockError()
                        },
                        onSuccess = {
                            rootDialogRepository.dismissDialog()
                            rootConfirmationRepository.showSuccess(
                                message = "User has been blocked!"
                            )
                            blockedUsersRepository.fetchBlockedUsers()
                            onBlockSuccess()
                        }
                    )
                },
                secondaryButtonText = "Cancel",
                onSecondaryButtonClick = {
                    rootDialogRepository.dismissDialog()
                },
                exitButton = true
            )
        )
    }

    protected fun showUnblockDialog(
        dialogRepository: RootDialogRepository,
        rootConfirmationRepository: RootConfirmationRepository,
        blockedUsersRepository: BlockedUsersRepository,
        onUnblockSuccess: () -> Unit = {},
        onUnblockError: () -> Unit = {},
        userId: String,
        name: String,
    ) {
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
                            userId = userId,
                            onError = {
                                dialogRepository.dismissDialog()
                                rootConfirmationRepository.showError()
                                onUnblockError()
                            },
                            onSuccess = {
                                dialogRepository.dismissDialog()
                                rootConfirmationRepository.showSuccess(
                                    message = "$name has been unblocked.",
                                )
                                blockedUsersRepository.fetchBlockedUsers()
                                onUnblockSuccess()
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

    protected fun contactSeller(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        profileRepository: ProfileRepository,
        postsRepository: ResellPostRepository,
        rootNavigationRepository: RootNavigationRepository,
        rootConfirmationRepository: RootConfirmationRepository,
        uid: String,
        id: String
    ) {
        viewModelScope.launch {
            try {
                val userInfo = profileRepository.getUserById(uid).user.toUserInfo()
                val post = postsRepository.allPostsFlow.first().asSuccess().let {
                    it.data.first {
                        it.id == id
                    }
                }.toListing()
                rootNavigationRepository.navigate(
                    ResellRootRoute.CHAT(
                        isBuyer = true,
                        name = userInfo.name,
                        pfp = userInfo.imageUrl,
                        email = userInfo.email,
                        postJson = Json.encodeToString(post),
                    )
                )

                delay(500)
                onSuccess()
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error navigating to chat: ", e)
                onError()
                rootConfirmationRepository.showError()
            }
        }
    }
}
