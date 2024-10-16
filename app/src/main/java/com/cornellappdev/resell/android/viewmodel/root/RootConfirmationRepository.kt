package com.cornellappdev.resell.android.viewmodel.root

import androidx.compose.ui.graphics.Color
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootConfirmationRepository @Inject constructor() {

    private val _confirmationSurfaceInfo =
        MutableStateFlow<UIEvent<ConfirmationSurfaceInfo>?>(null)
    val confirmationSurfaceInfo = _confirmationSurfaceInfo.asStateFlow()

    fun showSuccess(message: String, painterRes: Int? = null) {
        _confirmationSurfaceInfo.value = UIEvent(
            ConfirmationSurfaceInfo(
                painterRes = painterRes,
                title = message,
                color = ResellPurple
            )
        )
    }

    fun showError(
        message: String = "Something went wrong with your request. Please try again.",
        painterRes: Int? = R.drawable.ic_info) {
        _confirmationSurfaceInfo.value = UIEvent(
            ConfirmationSurfaceInfo(
                painterRes = painterRes,
                title = message,
                color = Secondary
            )
        )
    }
}

data class ConfirmationSurfaceInfo(
    val painterRes: Int? = null,
    val color: Color = ResellPurple,
    val title: String,
)
