package com.cornellappdev.resell.android.viewmodel.main

import com.cornellappdev.resell.android.model.messages.Chat
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.justinChats
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
) :
    ResellViewModel<MessagesViewModel.MessagesUiState>(
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
    )


    enum class ChatType {
        Purchases, Offers
    }

    fun onMessagePressed(chat: Chat) {
        applyMutation {
            copy(currentChat = chat)
        }
        navController.navigate(ResellRootRoute.CHAT)
    }

    fun onChangeChatType(chatType: ChatType) {
        applyMutation {
            copy(chatType = chatType)
        }
    }
}
