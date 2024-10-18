package com.cornellappdev.resell.android.viewmodel.root

import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootDialogRepository @Inject constructor() {

    private val _showDialogEvent: MutableStateFlow<UIEvent<RootDialogContent>?> =
        MutableStateFlow(null)
    val showDialogEvent = _showDialogEvent.asStateFlow()

    private val _hideDialogEvent: MutableStateFlow<UIEvent<Unit>?> = MutableStateFlow(null)
    val hideDialogEvent = _hideDialogEvent.asStateFlow()

    private val _primaryButtonStateEvent: MutableStateFlow<UIEvent<ResellTextButtonState>?> =
        MutableStateFlow(null)
    val primaryButtonStateEvent = _primaryButtonStateEvent.asStateFlow()

    private val _secondaryButtonStateEvent: MutableStateFlow<UIEvent<ResellTextButtonState>?> =
        MutableStateFlow(null)
    val secondaryButtonStateEvent = _secondaryButtonStateEvent.asStateFlow()

    fun showDialog(event: RootDialogContent) {
        _showDialogEvent.value = UIEvent(event)
    }

    fun dismissDialog() {
        _hideDialogEvent.value = UIEvent(Unit)
    }

    fun setPrimaryButtonState(state: ResellTextButtonState) {
        _primaryButtonStateEvent.value = UIEvent(state)
    }

    fun setSecondaryButtonState(state: ResellTextButtonState) {
        _secondaryButtonStateEvent.value = UIEvent(state)
    }
}
