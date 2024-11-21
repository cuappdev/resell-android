package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val userInfoRepository: UserInfoRepository
) {

    private val _buyersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<BuyerSellerData>>>(ResellApiResponse.Pending)
    val buyersHistoryFlow = _buyersHistoryFlow.asStateFlow()

    private val _sellersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<BuyerSellerData>>>(ResellApiResponse.Pending)
    val sellersHistoryFlow = _sellersHistoryFlow.asStateFlow()

    private val _subscribedChatFlow =
        MutableStateFlow<ResellApiResponse<Chat>>(ResellApiResponse.Pending)
    val subscribedChatFlow = _subscribedChatFlow.asStateFlow()

    /**
     * Starts loading the chat history and sends it down [sellersHistoryFlow].
     */
    fun fetchSellersHistory() {
        _sellersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sellerData =
                    fireStoreRepository.getSellerHistory(userInfoRepository.getEmail()!!)
                _sellersHistoryFlow.value = ResellApiResponse.Success(sellerData)
            } catch (e: Exception) {
                _sellersHistoryFlow.value = ResellApiResponse.Error
                Log.e("ChatRepository", "Error fetching buyer history: ", e)
            }
        }
    }

    /**
     * Starts loading the chat history and sends it down [buyersHistoryFlow].
     */
    fun fetchBuyersHistory() {
        _buyersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val buyerData =
                    fireStoreRepository.getBuyerHistory(userInfoRepository.getEmail()!!)
                _buyersHistoryFlow.value = ResellApiResponse.Success(buyerData)
            } catch (e: Exception) {
                _buyersHistoryFlow.value = ResellApiResponse.Error
                Log.e("ChatRepository", "Error fetching buyer history: ", e)
            }
        }
    }

    fun subscribeToChat(myEmail: String, otherEmail: String, myId: String) {
        fireStoreRepository.subscribeToChat(myEmail, otherEmail) {
            // Convert the List<ChatDocument> into a Chat
            var otherPfp = ""

            // Step 1: Creates a list of Pair<ChatMessageData, String>.
            // The String is the sender's id.
            val messageData = it.map { document ->
                // TODO: Cannot become `State`... dunno what that is.
                val messageType =
                    if (document.image.isNotEmpty()) {
                        MessageType.Image
                    } else if (document.availability != null) {
                        MessageType.Availability
                    } else if (document.product != null) {
                        MessageType.Card
                    } else {
                        MessageType.Message
                    }

                if (document.user.id != myId) {
                    otherPfp = document.user.avatar
                }

                Pair(
                    ChatMessageData(
                        id = document.id,
                        content = document.text,
                        timestampString = document.createdAt,
                        messageType = messageType
                    ), document.user.id
                )
            }

            // Step 2: Cluster by sender.
            val messageClusters = mutableListOf<ChatMessageCluster>()

            var currentList = mutableListOf<ChatMessageData>()
            var currentSender = ""

            messageData.forEach { (message, sender) ->
                if (sender != currentSender) {
                    if (currentSender.isNotEmpty()) {
                        messageClusters.add(
                            ChatMessageCluster(
                                fromUser = currentSender == myEmail,
                                senderId = currentSender,
                                messages = currentList.toImmutableList(),
                                senderImage = otherPfp
                            )
                        )
                    }
                    currentList = mutableListOf(message)
                    currentSender = sender
                } else {
                    currentList.add(message)
                }
            }

            if (currentSender.isNotEmpty()) {
                messageClusters.add(
                    ChatMessageCluster(
                        fromUser = currentSender == myEmail,
                        senderId = currentSender,
                        messages = currentList.toImmutableList(),
                        senderImage = otherPfp
                    )
                )
            }

            // Step 3: Return the final Chat object.
            val chat = Chat(
                chatHistory = messageClusters
            )

            _subscribedChatFlow.value = ResellApiResponse.Success(chat)
        }
    }
}
