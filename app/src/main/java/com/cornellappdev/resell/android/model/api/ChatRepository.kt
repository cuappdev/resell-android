package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.UserDocument
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val userInfoRepository: UserInfoRepository,
    private val postRepository: ResellPostRepository,
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

    fun subscribeToChat(myEmail: String, otherEmail: String, myId: String, selfIsBuyer: Boolean) {
        fireStoreRepository.subscribeToChat(
            sellerEmail = if (selfIsBuyer) otherEmail else myEmail,
            buyerEmail = if (selfIsBuyer) myEmail else otherEmail
        ) {
            Log.d("helpme", it.toString())
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

                if (document.user._id != myId) {
                    otherPfp = document.user.avatar
                }

                Pair(
                    ChatMessageData(
                        id = document._id,
                        content = document.text,
                        timestampString = document.createdAt,
                        messageType = messageType
                    ), document.user._id
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

            Log.d("helpme", messageClusters.toString())

            // Step 3: Return the final Chat object.
            val chat = Chat(
                chatHistory = messageClusters
            )

            Log.d("helpme", chat.toString())

            _subscribedChatFlow.value = ResellApiResponse.Success(chat)
        }
    }

    suspend fun sendTextMessage(
        myEmail: String,
        otherEmail: String,
        myName: String,
        otherName: String,
        myImageUrl: String,
        otherImageUrl: String,
        text: String,
        selfIsBuyer: Boolean,
        postId: String,
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        val userInfo = userInfoRepository.getUserInfo()

        val sellerEmail = if (selfIsBuyer) otherEmail else myEmail
        val buyerEmail = if (selfIsBuyer) myEmail else otherEmail

        val sellerName = if (selfIsBuyer) otherName else myName
        val buyerName = if (selfIsBuyer) myName else otherName

        val sellerImageUrl = if (selfIsBuyer) otherImageUrl else myImageUrl
        val buyerImageUrl = if (selfIsBuyer) myImageUrl else otherImageUrl

        val time = getFormattedTime()

        val userDocument = UserDocument(
            _id = myEmail,
            name = userInfo.name,
            avatar = userInfo.imageUrl
        )
        val chatDocument = ChatDocument(
            _id = currentTimeMillis.toString(),
            createdAt = time,
            image = "",
            text = text,
            user = userDocument,
            availability = null,
            product = null
        )

        fireStoreRepository.sendTextMessage(
            buyerEmail = buyerEmail,
            sellerEmail = sellerEmail,
            chatDocument = chatDocument
        )

        val item = (postRepository.allPostsFlow.value.asSuccessOrNull()?.data?.firstOrNull {
            it.id == postId
        } ?: postRepository.getPostById(postId)).copy(
            id = postId,
        )

        // The data that will go into the `seller` entry of the buyer. Amazing.
        //  Basically, this belongs the buyer.
        //  SO, this should be information about the seller.
        val sellerData = BuyerSellerData(
            item = item,
            recentMessage = text,
            viewed = selfIsBuyer,
            name = sellerName,
            image = sellerImageUrl,
            recentMessageTime = time,
            recentSender = myName,
            confirmedTime = "",
            confirmedViewed = false,
        )

        // Information about the buyer. Shown to the seller.
        val buyerData = BuyerSellerData(
            item = item,
            recentMessage = text,
            viewed = !selfIsBuyer,
            name = buyerName,
            image = buyerImageUrl,
            recentMessageTime = time,
            recentSender = myName,
            confirmedTime = "",
            confirmedViewed = false,
        )

        fireStoreRepository.updateBuyerHistory(
            buyerEmail = buyerEmail,
            sellerEmail = sellerEmail,
            data = buyerData
        )

        fireStoreRepository.updateSellerHistory(
            buyerEmail = buyerEmail,
            sellerEmail = sellerEmail,
            data = sellerData
        )

        fireStoreRepository.updateItems(
            email = buyerEmail,
            postId = postId,
            post = item
        )
    }

    private fun getFormattedTime(): String {
        // Get the current time in the system's time zone (you can change ZoneId for specific time zones)
        val currentTime = ZonedDateTime.now(ZoneId.of("America/New_York"))

        // Define the desired format
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm:ss a z")

        // Format the current time according to the specified pattern
        return currentTime.format(formatter)
    }
}
