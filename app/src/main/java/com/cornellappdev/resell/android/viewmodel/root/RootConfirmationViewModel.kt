package com.cornellappdev.resell.android.viewmodel.root

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootConfirmationViewModel @Inject constructor(
    private val rootConfirmationRepository: RootConfirmationRepository
) : ResellViewModel
<RootConfirmationViewModel.ConfirmationUiState>(
    initialUiState = ConfirmationUiState()
) {

    data class ConfirmationUiState(
        val show: Boolean = false,
        val title: String = "",
        val painterRes: Int? = null,
        val color: Color = ResellPurple,
        val id: Int = -1,
    )

    private fun onTimeout(id: Int) {
        if (id == stateValue().id) {
            onDismiss()
        }
    }

    fun onDismiss() {
        applyMutation {
            copy(
                show = false
            )
        }
    }

    init {
        asyncCollect(rootConfirmationRepository.confirmationSurfaceInfo) { event ->
            event?.consume { info ->
                val randomId = System.currentTimeMillis().toInt()
                applyMutation {
                    copy(
                        show = true,
                        title = info.title,
                        painterRes = info.painterRes,
                        color = info.color,
                        id = randomId
                    )
                }

                viewModelScope.launch {
                    delay(3000)
                    onTimeout(randomId)
                }
            }
        }
    }
}
