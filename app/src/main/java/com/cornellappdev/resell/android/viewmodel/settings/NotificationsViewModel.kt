package com.cornellappdev.resell.android.viewmodel.settings

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.settings.NotificationsLocalRepository
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsLocalRepository: NotificationsLocalRepository,
) :
    ResellViewModel<NotificationsViewModel.NotificationsState>
        (
        initialUiState = NotificationsState()
    ) {

    init {
        asyncCollect(notificationsLocalRepository.allNotificationsEnabled) { enabled ->
            applyMutation {
                copy(pauseAllNotifications = !enabled)
            }
        }

        asyncCollect(notificationsLocalRepository.chatNotificationsEnabled) { enabled ->
            applyMutation {
                copy(chatRoot = enabled)
            }
        }

        asyncCollect(notificationsLocalRepository.listingsNotificationsEnabled) { enabled ->
            applyMutation {
                copy(listingsRoot = enabled)
            }
        }
    }

    data class NotificationsState(
        val pauseAllNotifications: Boolean = false,
        val chatRoot: Boolean = false,
        val listingsRoot: Boolean = false
    ) {

        val chat
            get() = chatRoot && !pauseAllNotifications

        val newListings
            get() = listingsRoot && !pauseAllNotifications
    }

    fun onTogglePauseAllNotifications(checked: Boolean) {
        viewModelScope.launch {
            notificationsLocalRepository.setNotificationsEnabled(
                !checked
            )
        }
    }

    fun onToggleChatNotifications(checked: Boolean) {
        viewModelScope.launch {
            notificationsLocalRepository.setChatNotificationsEnabled(
                checked
            )
        }
    }

    fun onToggleNewListingsNotifications(checked: Boolean) {
        viewModelScope.launch {
            notificationsLocalRepository.setListingsNotificationsEnabled(
                checked
            )
        }
    }
}
