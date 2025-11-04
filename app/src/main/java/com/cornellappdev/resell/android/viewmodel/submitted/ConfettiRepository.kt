package com.cornellappdev.resell.android.viewmodel.submitted

import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfettiRepository @Inject constructor() {
    private val _showConfettiEvent: MutableStateFlow<UIEvent<ConfettiViewModel.ConfettiUiState>?> =
        MutableStateFlow(null)
    val showConfettiEvent = _showConfettiEvent.asStateFlow()

    fun showConfetti (event: ConfettiViewModel.ConfettiUiState) {
        _showConfettiEvent.value = UIEvent(event)
    }
}