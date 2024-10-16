package com.cornellappdev.resell.android.viewmodel.root

import android.util.Log
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootDialogViewModel @Inject constructor(
    rootDialogRepository: RootDialogRepository
) :
    ResellViewModel<RootDialogViewModel.RootDialogUiState>(
        initialUiState = RootDialogUiState()
    ) {
    data class RootDialogUiState(
        val content: RootDialogContent? = null,
        val showing: Boolean = false
    )

    fun onDismiss() {
        applyMutation { copy(showing = false) }
    }

    init {
        asyncCollect(rootDialogRepository.showDialogEvent) { event ->
            event?.consume {
                Log.d("helpme", "event: $event")
                applyMutation {
                    copy(
                        content = event.payload,
                        showing = true,
                    )
                }
            }
        }

        asyncCollect(rootDialogRepository.hideDialogEvent) { event ->
            event?.consume {
                applyMutation {
                    copy(
                        showing = false,
                    )
                }
            }
        }

        asyncCollect(rootDialogRepository.primaryButtonStateEvent) { event ->
            event?.consume { state ->
                applyMutation {
                    when (this.content) {
                        is RootDialogContent.TwoButtonDialog -> copy(
                            content = this.content.copy(
                                primaryButtonState = state
                            )
                        )

                        is RootDialogContent.CorrectAnswerDialog -> copy(
                            content = this.content.copy(
                                primaryButtonState = state
                            )
                        )

                        else -> copy()
                    }
                }
            }
        }

        asyncCollect(rootDialogRepository.secondaryButtonStateEvent) { event ->
            event?.consume { state ->
                applyMutation {
                    when (this.content) {
                        is RootDialogContent.TwoButtonDialog -> copy(
                            content = this.content.copy(
                                secondaryButtonState = state
                            )
                        )

                        is RootDialogContent.CorrectAnswerDialog -> copy(
                            content = this.content.copy(
                                secondaryButtonState = state
                            )
                        )

                        else -> copy()
                    }
                }
            }
        }
    }
}

sealed class RootDialogContent {
    data class TwoButtonDialog(
        val title: String,
        val description: String,
        val primaryButtonText: String,
        val secondaryButtonText: String?,
        val onPrimaryButtonClick: () -> Unit,
        val onSecondaryButtonClick: () -> Unit,
        val exitButton: Boolean,
        val primaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
        val secondaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.NAKED_APPDEV,
        val primaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
        val secondaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED
    ) : RootDialogContent()

    data class CorrectAnswerDialog(
        val title: String,
        val description: String,
        val correctAnswer: String,
        val primaryButtonText: String,
        val secondaryButtonText: String?,
        val onPrimaryButtonClick: () -> Unit,
        val onSecondaryButtonClick: () -> Unit,
        val exitButton: Boolean,
        val primaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
        val secondaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.NAKED_APPDEV,
        val primaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
        val secondaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED
    ) : RootDialogContent()
}
