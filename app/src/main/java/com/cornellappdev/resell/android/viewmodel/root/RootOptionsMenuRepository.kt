package com.cornellappdev.resell.android.viewmodel.root

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootOptionsMenuRepository @Inject constructor() {

    private val _optionsEvent = MutableStateFlow<UIEvent<RootOptionsMenuViewModel.UiState>?>(null)
    val optionsEvent = _optionsEvent.asStateFlow()

    private val _hideEvent = MutableStateFlow<UIEvent<Unit>?>(null)
    val hideEvent = _hideEvent.asStateFlow()

    fun showOptionsMenu(
        fromTop: Dp = 98.dp,
        options: List<OptionType> = listOf(
            OptionType.SHARE,
            OptionType.REPORT,
            OptionType.BLOCK,
        ),
        alignment: Alignment = Alignment.TopStart,
        callback: (OptionType) -> Unit,
    ) {
        _optionsEvent.value = UIEvent(
            RootOptionsMenuViewModel.UiState(
                options = options,
                callback = callback,
                fromTop = fromTop,
                alignment = alignment,
                showing = true
            )
        )
    }

    fun hideOptionsMenu() {
        _hideEvent.value = UIEvent(Unit)
    }
}
