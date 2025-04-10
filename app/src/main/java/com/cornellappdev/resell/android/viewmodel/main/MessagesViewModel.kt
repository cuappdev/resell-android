package com.cornellappdev.resell.android.viewmodel.main

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.chats.ChatHeaderData
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.util.parseIsoDateToDate
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel.ChatType
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val postsRepository: ResellPostRepository,
) :
    ResellViewModel<MessagesViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chatType = ChatType.Purchases,
            buyerChats = ResellApiResponse.Pending,
            sellerChats = ResellApiResponse.Pending,
        )
    ) {
    data class MessagesUiState(
        /** People who are buying things you are selling. */
        val buyerChats: ResellApiResponse<List<ChatHeaderData>>,
        /** People who are selling things you are buying. */
        val sellerChats: ResellApiResponse<List<ChatHeaderData>>,
        val chatType: ChatType,
    ) {
        val filteredChats = when (chatType) {
            ChatType.Purchases -> sellerChats
            ChatType.Offers -> buyerChats
        }.let {
            if (it is ResellApiResponse.Success) {
                it.data
            } else {
                emptyList()
            }
        }.sortedByDescending {
            parseIsoDateToDate(it.recentMessageTime)
        }

        val loadedState: ResellApiState =
            if (buyerChats is ResellApiResponse.Success && sellerChats is ResellApiResponse.Success) {
                ResellApiState.Success
            } else if (buyerChats is ResellApiResponse.Error || sellerChats is ResellApiResponse.Error) {
                ResellApiState.Error
            } else {
                ResellApiState.Loading
            }

        val purchasesUnreads: Int
            get() = sellerChats.asSuccessOrNull()?.data?.filter { !it.viewed }?.size ?: 0

        val offersUnreads: Int
            get() = buyerChats.asSuccessOrNull()?.data?.filter { !it.viewed }?.size ?: 0
    }

    fun onMessagePressed(historyEntry: ChatHeaderData) {
        val id = historyEntry.item.toListing().id

        contactSeller(
            onSuccess = {},
            onError = {},
            profileRepository = profileRepository,
            postsRepository = postsRepository,
            rootConfirmationRepository = rootConfirmationRepository,
            rootNavigationRepository = rootNavigationRepository,
            id = id,
            isBuyer = stateValue().chatType == ChatType.Purchases,
            name = historyEntry.name,
            email = historyEntry.email ?: "",
            pfp = historyEntry.image
        )

        // Wait a bit then reload; loads the marked as read.
        viewModelScope.launch {
            delay(400)
            onLoad()
        }
    }

    fun onChangeChatType(chatType: ChatType) {
        applyMutation {
            copy(chatType = chatType)
        }
    }

    fun onLoad() {
        chatRepository.fetchBuyersHistory()
        chatRepository.fetchSellersHistory()
    }

    init {
        asyncCollect(chatRepository.buyersHistoryFlow) { response ->
            applyMutation {
                copy(buyerChats = response)
            }
        }

        asyncCollect(chatRepository.sellersHistoryFlow) { response ->
            applyMutation {
                copy(sellerChats = response)
            }
        }

        onLoad()
    }
}
