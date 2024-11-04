package com.cornellappdev.resell.android.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.settings.BlockedUsersRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        onBlockError: () -> Unit = {}
    ) {
        rootDialogRepository.showDialog(
            RootDialogContent.TwoButtonDialog(
                title = "Block User",
                description = "Are you sure you’d like to block this user?",
                primaryButtonText = "Block",
                onPrimaryButtonClick = {
                    rootDialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                    blockedUsersRepository.onBlockUser(
                        userId = "userId",
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
}
