package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.ui.text.AnnotatedString
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TwoButtonSheetViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository
) : ResellViewModel<RootSheet.TwoButtonSheet>(
    initialUiState = RootSheet.TwoButtonSheet(
        primaryText = "",
        secondaryText = "",
        primaryCallback = {},
        secondaryCallback = {},
        title = "",
        description = AnnotatedString(""),
    )
) {
    init {
        asyncCollect(rootNavigationSheetRepository.rootSheetFlow) { uiEvent ->
            if (uiEvent == null || uiEvent.payload !is RootSheet.TwoButtonSheet) {
                return@asyncCollect
            }

            applyMutation {
                copy(
                    primaryText = uiEvent.payload.primaryText,
                    secondaryText = uiEvent.payload.secondaryText,
                    primaryCallback = uiEvent.payload.primaryCallback,
                    secondaryCallback = uiEvent.payload.secondaryCallback,
                    title = uiEvent.payload.title,
                    description = uiEvent.payload.description,
                    primaryContainerType = uiEvent.payload.primaryContainerType,
                    secondaryContainerType = uiEvent.payload.secondaryContainerType,
                    textAlign = uiEvent.payload.textAlign,
                    primaryButtonState = uiEvent.payload.primaryButtonState,
                    secondaryButtonState = uiEvent.payload.secondaryButtonState
                )
            }
        }
    }
}
