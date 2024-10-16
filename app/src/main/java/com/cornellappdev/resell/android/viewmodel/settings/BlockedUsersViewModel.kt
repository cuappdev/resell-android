package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsNavigationRepository: SettingsNavigationRepository,
) : ResellViewModel<BlockedUsersViewModel.BlockedUsersUiState>(
    initialUiState = BlockedUsersUiState(),
) {

    data class BlockedUsersUiState(
        val blockedUsers: List<UiBlockedUser> = emptyList(),
    )

    data class UiBlockedUser(
        val id: String,
        val name: String,
        val imageUrl: String,
    )

    fun onUnblock(id: String) {
        // TODO: Implement with dialog
    }
}
