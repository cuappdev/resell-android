package com.cornellappdev.resell.android.viewmodel.pdp

import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoldRepository @Inject constructor() {
    private val _showSoldEvent: MutableStateFlow<UIEvent<SoldViewModel.SoldUiState>?> =
        MutableStateFlow(null)

    val showSoldEvent = _showSoldEvent.asStateFlow()

    fun showSold(event: SoldViewModel.SoldUiState) {
        _showSoldEvent.value = UIEvent(event)
    }
}