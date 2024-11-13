package com.cornellappdev.resell.android.viewmodel.root

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootOptionsMenuViewModel @Inject constructor(
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
) : ResellViewModel<RootOptionsMenuViewModel.UiState>(
    initialUiState = UiState(options = emptyList())
) {
    data class UiState(
        val options: List<OptionType>,
        val callback: ((OptionType) -> Unit)? = null,
        val fromTop: Dp = 98.dp,
        val alignment: Alignment = Alignment.TopEnd,
        val showing: Boolean = false,
    )

    fun onOptionClicked(option: OptionType) {
        stateValue().callback?.invoke(option)
        onDismiss()
    }

    fun onDismiss() {
        rootOptionsMenuRepository.hideOptionsMenu()
    }

    init {
        asyncCollect(rootOptionsMenuRepository.optionsEvent) { event ->
            event?.consume {
                applyMutation { it }
            }
        }

        asyncCollect(rootOptionsMenuRepository.hideEvent) { event ->
            event?.consume {
                applyMutation { copy(showing = false) }
            }
        }
    }
}

enum class OptionType(
    val title: String,
    val icon: Int,
    val color: Color = Primary
) {
    SHARE("Share", R.drawable.ic_share),
    REPORT("Report", R.drawable.ic_report),
    BLOCK("Block", R.drawable.ic_slash),
    DELETE("Delete", R.drawable.ic_trash, Color.Red), ;
}
