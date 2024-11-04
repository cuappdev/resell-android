package com.cornellappdev.resell.android.model.settings

import com.cornellappdev.resell.android.util.richieUrl
import com.cornellappdev.resell.android.viewmodel.settings.BlockedUsersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedUsersRepository @Inject constructor() {

    fun fetchBlockedUsers(callback: (List<BlockedUsersViewModel.UiBlockedUser>) -> Unit) {
        // TODO: Implement
        callback(
            listOf(
                BlockedUsersViewModel.UiBlockedUser(
                    id = "0", name = "sunshine.chef", imageUrl = richieUrl
                )
            )
        )
    }

    fun onBlockUser(userId: String, onError: () -> Unit, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: Implement
            delay(1000)
            onSuccess()
        }
    }

    fun onUnblockUser(userId: String, onError: () -> Unit, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: Implement
            delay(1000)
            onSuccess()
        }
    }
}
