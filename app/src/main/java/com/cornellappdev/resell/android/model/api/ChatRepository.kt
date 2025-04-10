package com.cornellappdev.resell.android.model.api

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.ChatHeaderData
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.RawChatHeaderData
import com.cornellappdev.resell.android.model.chats.UserDocument
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.util.toDateString
import com.cornellappdev.resell.android.util.toIsoString
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.toImmutableList
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val userInfoRepository: UserInfoRepository,
    private val postRepository: ResellPostRepository,
    private val retrofitInstance: RetrofitInstance,
    private val googleAuthRepository: GoogleAuthRepository,
    private val dataStore: DataStore<Preferences>,
    private val profileRepository: ProfileRepository,
) {

    private val _buyersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<ChatHeaderData>>>(ResellApiResponse.Pending)
    val buyersHistoryFlow = _buyersHistoryFlow.asStateFlow()

    private val _sellersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<ChatHeaderData>>>(ResellApiResponse.Pending)
    val sellersHistoryFlow = _sellersHistoryFlow.asStateFlow()

    private val _subscribedChatFlow =
        MutableStateFlow<ResellApiResponse<Chat>>(ResellApiResponse.Pending)
    val subscribedChatFlow = _subscribedChatFlow.asStateFlow()

    /**
     * Starts the subscription to the chat history for the buyer.
     *
     * Whenever an update would occur, it will be sent down [buyersHistoryFlow].
     */
    fun subscribeToBuyerHistory() {
        _buyersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            val myId = userInfoRepository.getUserId() ?: ""
            fireStoreRepository.subscribeToBuyerHistory(myId = myId) { rawData ->
                CoroutineScope(Dispatchers.IO).launch {
                    val (listings, users) = getListingAndUserData(rawData, myId)
                    val chatHeaders = rawData.map {
                        val user = users.firstOrNull { user ->
                            user.user.id != myId
                        }?.user

                        val item = listings.firstOrNull { post ->
                            post.id == it.listingID
                        }?.toListing()

                        ChatHeaderData(
                            recentMessage = it.lastMessage,
                            updatedAt = it.updatedAt.toDateString(),
                            // TODO:
                            read = false,
                            name = item?.title ?: "",
                            imageUrl = user?.photoUrl ?: "",
                        )
                    }

                    _buyersHistoryFlow.value = ResellApiResponse.Success(chatHeaders)
                }
            }
        }
    }

    /**
     * Starts the subscription to the chat history for the seller.
     *
     * Whenever an update would occur, it will be sent down [sellersHistoryFlow].
     */
    fun subscribeToSellerHistory() {
        _sellersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            val myId = userInfoRepository.getUserId() ?: ""
            fireStoreRepository.subscribeToSellerHistory(myId = myId) { rawData ->
                CoroutineScope(Dispatchers.IO).launch {
                    val (listings, users) = getListingAndUserData(rawData, myId)
                    val chatHeaders = rawData.map {
                        val user = users.firstOrNull { user ->
                            user.user.id != myId
                        }?.user

                        val item = listings.firstOrNull { post ->
                            post.id == it.listingID
                        }?.toListing()

                        ChatHeaderData(
                            recentMessage = it.lastMessage,
                            updatedAt = it.updatedAt.toDateString(),
                            // TODO: Add actual read/unread logic
                            read = true,
                            name = item?.title ?: "",
                            imageUrl = user?.photoUrl ?: "",
                        )
                    }

                    _sellersHistoryFlow.value = ResellApiResponse.Success(chatHeaders)
                }
            }
        }
    }

    private suspend fun getListingAndUserData(
        rawData: List<RawChatHeaderData>,
        myId: String
    ): Pair<List<Post>, List<UserResponse>> = coroutineScope {
        val listingIds = rawData.map {
            it.listingID
        }
        val otherUserIds = rawData.map {
            it.userIDs.first {
                it != myId
            }
        }

        val deferredListings = listingIds.map { id ->
            async {
                postRepository.getPostById(id)
            }
        }

        val deferredUsers = otherUserIds.map { id ->
            async {
                profileRepository.getUserById(id)
            }
        }

        val allDeferred = deferredListings + deferredUsers

        val data = allDeferred.awaitAll()
        val listings = data.filterIsInstance<Post>()
        val userResponses = data.filterIsInstance<UserResponse>()

        return@coroutineScope Pair(listings, userResponses)
    }

    fun subscribeToChat(
        myEmail: String,
        otherEmail: String,
        selfIsBuyer: Boolean,
        myName: String,
        otherName: String
    ) {
        fireStoreRepository.subscribeToChat(
            sellerEmail = if (selfIsBuyer) otherEmail else myEmail,
            buyerEmail = if (selfIsBuyer) myEmail else otherEmail
        ) {
            // Convert the List<ChatDocument> into a Chat
            var otherPfp = ""

            // Step 1: Creates a list of Pair<ChatMessageData, String>.
            // The String is the sender's id.
            val messageData = it.map { document ->
                val messageType =
                    if (document.image.isNotEmpty()) {
                        MessageType.Image
                    } else if (document.availability != null) {
                        MessageType.Availability
                    } else if (document.product != null) {
                        MessageType.Card
                    } else if (document.meetingInfo != null) {
                        MessageType.State
                    } else {
                        MessageType.Message
                    }

                if (document.user._id != myEmail) {
                    otherPfp = document.user.avatar
                }

                Pair(
                    ChatMessageData(
                        id = document._id,
                        content = if (document.meetingInfo != null) {
                            getMeetingInfoContent(
                                document.meetingInfo,
                                document,
                                myEmail,
                                otherName
                            )
                        } else document.text,
                        timestamp = document.createdAt,
                        senderEmail = document.user._id,
                        messageType = messageType,
                        imageUrl = document.image,
                        post = document.product,
                        availability = document.availability,
                        meetingInfo = document.meetingInfo
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
                                senderImage = otherPfp,
                                senderName = if (currentSender == myEmail) myName else otherName
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
                        senderImage = otherPfp,
                        senderName = if (currentSender == myEmail) myName else otherName
                    )
                )
            }

            // Step 2a: Insert "MONTH DAY, YEAR" states whenever a new day starts.
            //  A new day can start within a cluster, keep in mind.
            var lastTimestamp = Timestamp(0, 0)

            val dateStateClusters = messageClusters.map { cluster ->
                var newCluster = cluster.copy()
                val newMessages = cluster.messages.toMutableList()
                cluster.messages.forEach { messageData ->
                    if (lastTimestamp.toDate().day != messageData.timestamp.toDate().day
                        || lastTimestamp.toDate().month != messageData.timestamp.toDate().month
                        || lastTimestamp.toDate().year != messageData.timestamp.toDate().year
                    ) {
                        newMessages.add(
                            newMessages.indexOf(messageData),
                            ChatMessageData(
                                id = "",
                                content = messageData.timestamp.toDateString(),
                                timestamp = messageData.timestamp,
                                messageType = MessageType.State,
                                senderEmail = currentSender,
                            )
                        )
                        newCluster = newCluster.copy(messages = newMessages.toImmutableList())
                    }
                    lastTimestamp = messageData.timestamp
                }

                newCluster
            }

            // Step 2b: Make the final occurrence of `MeetingInfo`'s mostRecent field true.
            dateStateClusters.map { cluster ->
                cluster.messages
            }.flatten()
                .filter {
                    it.meetingInfo != null
                }.sortedByDescending {
                    it.timestamp
                }.firstOrNull()?.meetingInfo?.mostRecent = true


            // Step 3: Return the final Chat object.
            val chat = Chat(
                chatHistory = dateStateClusters
            )

            _subscribedChatFlow.value = ResellApiResponse.Success(chat)
        }
    }

    suspend fun markChatRead(
        myEmail: String,
        otherEmail: String,
        chatType: ChatViewModel.ChatType,
    ) {
        fireStoreRepository.markChatAsRead(
            myEmail = myEmail,
            otherEmail = otherEmail,
            chatType = chatType
        )
    }

    private fun getMeetingInfoContent(
        meetingInfo: MeetingInfo,
        document: ChatDocument,
        myEmail: String,
        otherName: String
    ) = when (meetingInfo.state) {
        "proposed" -> {
            if (document.user._id == myEmail) {
                "You proposed a new meeting"
            } else {
                "$otherName proposed a new meeting"
            }
        }

        "confirmed" -> {
            if (document.user._id == myEmail) {
                "You accepted a new meeting"
            } else {
                "$otherName accepted a new meeting"
            }
        }

        "declined" -> {
            if (document.user._id == myEmail) {
                "You declined the meeting proposal"
            } else {
                "$otherName declined the meeting proposal"
            }
        }

        "canceled" -> {
            if (document.user._id == myEmail) {
                "You canceled the meeting"
            } else {
                "$otherName canceled the meeting"
            }
        }

        else -> {
            ""
        }
    }

    private suspend fun sendGenericMessage(
        myEmail: String,
        otherEmail: String,
        myName: String,
        otherName: String,
        myImageUrl: String,
        otherImageUrl: String,
        selfIsBuyer: Boolean,
        postId: String,
        imageUrl: String? = null,
        text: String? = null,
        availability: AvailabilityDocument? = null,
        meetingInfo: MeetingInfo? = null
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        val userInfo = userInfoRepository.getUserInfo()

        val sellerEmail = if (selfIsBuyer) otherEmail else myEmail
        val buyerEmail = if (selfIsBuyer) myEmail else otherEmail

        val sellerName = if (selfIsBuyer) otherName else myName
        val buyerName = if (selfIsBuyer) myName else otherName

        val sellerImageUrl = if (selfIsBuyer) otherImageUrl else myImageUrl
        val buyerImageUrl = if (selfIsBuyer) myImageUrl else otherImageUrl

        val time = Timestamp.now()

        val userDocument = UserDocument(
            _id = myEmail,
            name = userInfo.name,
            avatar = userInfo.imageUrl
        )
        val chatDocument = ChatDocument(
            _id = currentTimeMillis.toString(),
            createdAt = time,
            image = imageUrl ?: "",
            text = text ?: "",
            user = userDocument,
            availability = availability,
            // Product handled later
            product = null,
            meetingInfo = meetingInfo,
        )

        val item = (postRepository.allPostsFlow.value.asSuccessOrNull()?.data?.firstOrNull {
            it.id == postId
        } ?: postRepository.getPostById(postId)).copy(
            id = postId,
        )

        // Before sending, if it's the first message in the chat, send a product message.
        val response = subscribedChatFlow.value
        if (response is ResellApiResponse.Success && response.data.chatHistory.isEmpty()) {
            fireStoreRepository.sendProductMessage(
                buyerEmail = buyerEmail,
                sellerEmail = sellerEmail,
                otherDocument = chatDocument,
                post = item
            )
        }

        fireStoreRepository.sendChatMessage(
            buyerEmail = buyerEmail,
            sellerEmail = sellerEmail,
            chatDocument = chatDocument
        )
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
    ) = sendGenericMessage(
        myEmail = myEmail,
        otherEmail = otherEmail,
        myName = myName,
        otherName = otherName,
        myImageUrl = myImageUrl,
        otherImageUrl = otherImageUrl,
        text = text,
        selfIsBuyer = selfIsBuyer,
        postId = postId,
    )

    suspend fun sendImageMessage(
        myEmail: String,
        otherEmail: String,
        myName: String,
        otherName: String,
        myImageUrl: String,
        otherImageUrl: String,
        imageBase64: String,
        selfIsBuyer: Boolean,
        postId: String,
    ) {
        val url = retrofitInstance.userApi.uploadImage(
            body = ImageBody(
                imageBase64 = imageBase64
            )
        ).image

        sendGenericMessage(
            myEmail = myEmail,
            otherEmail = otherEmail,
            myName = myName,
            otherName = otherName,
            myImageUrl = myImageUrl,
            otherImageUrl = otherImageUrl,
            imageUrl = url,
            selfIsBuyer = selfIsBuyer,
            postId = postId,
        )
    }

    suspend fun sendAvailability(
        myEmail: String,
        otherEmail: String,
        myName: String,
        otherName: String,
        myImageUrl: String,
        otherImageUrl: String,
        selfIsBuyer: Boolean,
        postId: String,
        availability: AvailabilityDocument
    ) = sendGenericMessage(
        availability = availability,
        myEmail = myEmail,
        otherEmail = otherEmail,
        myName = myName,
        otherName = otherName,
        myImageUrl = myImageUrl,
        otherImageUrl = otherImageUrl,
        selfIsBuyer = selfIsBuyer,
        postId = postId
    )

    /**
     * Depending on
     */
    suspend fun sendProposalUpdate(
        myEmail: String,
        otherEmail: String,
        myName: String,
        otherName: String,
        myImageUrl: String,
        otherImageUrl: String,
        selfIsBuyer: Boolean,
        postId: String,
        meetingInfo: MeetingInfo
    ) = sendGenericMessage(
        meetingInfo = meetingInfo,
        myEmail = myEmail,
        otherEmail = otherEmail,
        myName = myName,
        otherName = otherName,
        myImageUrl = myImageUrl,
        otherImageUrl = otherImageUrl,
        selfIsBuyer = selfIsBuyer,
        postId = postId
    )

    private fun getFormattedTime(): String {
        // Get the current time in the system's time zone (you can change ZoneId for specific time zones)
        val currentTime = ZonedDateTime.now(ZoneId.of("America/New_York"))

        // Define the desired format
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm:ss a z")

        // Format the current time according to the specified pattern
        return currentTime.format(formatter)
    }

    suspend fun shouldShowGCalSync(
        otherEmail: String,
        meetingDate: Date,
    ): Boolean {
        val mapString = dataStore.data.map { preferences ->
            preferences[PreferencesKeys.GCAL_SYNC]
        }.firstOrNull()

        val map = mapString?.let { Json.decodeFromString<Map<String, String>>(mapString) }

        if (map != null && map[otherEmail] == meetingDate.toString()) {
            return false
        }

        // Store new map, indicating that the user has already been notified for this meeting.
        val newMap = map?.toMutableMap() ?: mutableMapOf()
        newMap[otherEmail] = meetingDate.toString()
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GCAL_SYNC] = Json.encodeToString(newMap)
        }

        return true
    }
}
