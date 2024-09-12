package com.cornellappdev.android.resell.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
}
