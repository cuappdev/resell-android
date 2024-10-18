package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ResellApiState
import com.cornellappdev.resell.android.model.RootNav
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.justinChats
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @RootNav private val navController: NavHostController,
) :
    ResellViewModel<ChatViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chats = justinChats(40),
            loadedState = ResellApiState.Success,
            chatType = ChatType.Purchases,
            currentChat = null
        )
    ) {
    data class MessagesUiState(
        val loadedState: ResellApiState,
        val chats: List<Chat>,
        val chatType: ChatType,
        val currentChat: Chat?,
    ) {
        val filteredChats = chats.filter { it.chatType == chatType }
    }



    enum class ChatType {
        Purchases, Offers
    }

    fun onMessagePressed(chat: Chat) {
        applyMutation{
            copy (currentChat = chat)
        }
        navController.navigate(ResellRootRoute.CHAT)
    }

    fun onChangeChatType(chatType: ChatType) {
        applyMutation {
            copy (chatType = chatType)
        }
    }

    fun onBackPressed() {
        applyMutation {
            copy (currentChat = null)
        }
        navController.popBackStack()
    }
}
