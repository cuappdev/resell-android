package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatMeetingSheetViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
) : ResellViewModel<ChatMeetingSheetViewModel.ChatMeetingStateUi>(
    initialUiState = ChatMeetingStateUi()
) {

    init {
        asyncCollect(rootNavigationSheetRepository.rootSheetFlow) { uiEvent ->
            if (uiEvent == null || uiEvent.payload !is RootSheet.MeetingDetails) {
                return@asyncCollect
            }

            applyMutation {
                copy(
                    enabled = true,
                    callback = uiEvent.payload.callback,
                    title = uiEvent.payload.title,
                    confirmText = uiEvent.payload.confirmString,
                    closeText = uiEvent.payload.closeString,
                    confirmColor = uiEvent.payload.confirmColor,
                    content = uiEvent.payload.content
                )
            }
        }
    }

    data class ChatMeetingStateUi(
        val enabled: Boolean = false,
        val confirmText: String = "Confirm",
        val closeText: String = "Cancel",
        val confirmColor: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
        val title: String = "",
        val callback: () -> Unit = {},
        val content: @Composable () -> Unit = {}
    )

    fun onConfirmPressed() {
        // Hide the sheet and fire the callback.
        rootNavigationSheetRepository.hideSheet()
        stateValue().callback()
    }

    fun onClosePressed() {
        rootNavigationSheetRepository.hideSheet()
    }
}
