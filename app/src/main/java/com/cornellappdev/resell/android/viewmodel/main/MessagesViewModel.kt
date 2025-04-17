package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.chats.ChatHeaderData
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.model.classes.toResellApiState
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
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
    private val userInfoRepository: UserInfoRepository,
    private val fireStoreRepository: FireStoreRepository
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
        }.let { response ->
            if (response is ResellApiResponse.Success) {
                val list = response.data.sortedByDescending { headerData ->
                    headerData.updatedAt
                }
                ResellApiResponse.Success(list)
            } else {
                response
            }
        }

        val loadedState: ResellApiState
            get() = filteredChats.toResellApiState()

        val loadedEmpty: Boolean
            get() = filteredChats is ResellApiResponse.Success && filteredChats.data.isEmpty()

        val purchasesUnreads: Int
            get() = sellerChats.asSuccessOrNull()?.data?.filter { !it.read }?.size ?: 0

        val offersUnreads: Int
            get() = buyerChats.asSuccessOrNull()?.data?.filter { !it.read }?.size ?: 0
    }

    fun onMessagePressed(historyEntry: ChatHeaderData) = viewModelScope.launch {
        try {
            val myId = userInfoRepository.getUserId() ?: ""
            contactSeller(
                postsRepository = postsRepository,
                rootNavigationRepository = rootNavigationRepository,
                fireStoreRepository = fireStoreRepository,
                name = historyEntry.name,
                myId = myId,
                otherId = historyEntry.userId,
                pfp = historyEntry.imageUrl,
                listingId = historyEntry.listingId,
                isBuyer = stateValue().chatType == ChatType.Purchases
            )

            // Wait a bit then reload; loads the marked as read.
            delay(400)
            onLoad()
        } catch (e: Exception) {
            Log.e("MessagesViewModel", "Error navigating to chat: ", e)
            rootConfirmationRepository.showError()
        }
    }

    fun onChangeChatType(chatType: ChatType) {
        applyMutation {
            copy(chatType = chatType)
        }
    }

    fun onLoad() {
        chatRepository.subscribeToBuyerHistory()
        chatRepository.subscribeToSellerHistory()
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
