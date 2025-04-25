package com.cornellappdev.resell.android.model.api

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.chats.AvailabilityBlock
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.ChatHeaderData
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.RawChatHeaderData
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.util.toDateString
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
                    val chatHeaders = rawData.mapNotNull {
                        rawToRealHeaderData(users, myId, listings, it)
                    }

                    _buyersHistoryFlow.value = ResellApiResponse.Success(chatHeaders)
                }
            }
        }
    }

    private suspend fun rawToRealHeaderData(
        users: List<UserResponse>,
        myId: String,
        listings: List<Post>,
        it: RawChatHeaderData
    ): ChatHeaderData? {
        val otherId = if (it.buyerID == myId) it.sellerID else it.buyerID
        val user = users.firstOrNull { user ->
            user.user.id == otherId
        }?.user

        val item = listings.firstOrNull { listing ->
            listing.id == it.listingID
        }
        return item?.toListing()?.let { listing ->
            ChatHeaderData(
                recentMessage = it.lastMessage,
                updatedAt = it.updatedAt,
                read = fireStoreRepository.getMostRecentMessageRead(
                    it.chatID,
                    myId
                ),
                name = user?.username ?: "",
                imageUrl = user?.photoUrl ?: "",
                listing = listing,
                chatId = it.chatID,
                userId = user?.id ?: "",
            )
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
                    val chatHeaders = rawData.mapNotNull {
                        rawToRealHeaderData(users, myId, listings, it)
                    }

                    _sellersHistoryFlow.value = ResellApiResponse.Success(chatHeaders)
                }
            }
        }
    }

    /**
     * Asynchronously loads all users and listings for the given [rawData] in parallel.
     */
    private suspend fun getListingAndUserData(
        rawData: List<RawChatHeaderData>,
        myId: String
    ): Pair<List<Post>, List<UserResponse>> = coroutineScope {
        val listingIds = rawData.map {
            it.listingID
        }.toSet()
        val otherUserIds = rawData.map {
            it.userIDs.first {
                it != myId
            }
        }.toSet()

        val deferredListings = listingIds.map { id ->
            async {
                try {
                    postRepository.getPostById(id)
                } catch (e: Exception) {
                    Log.e("ChatRepository", "Error fetching listing: ", e)
                    null
                }
            }
        }

        val deferredUsers = otherUserIds.map { id ->
            async {
                try {
                    profileRepository.getUserById(id)
                } catch (e: Exception) {
                    Log.e("ChatRepository", "Error fetching user: ", e)
                    null
                }
            }
        }

        val allDeferred = deferredListings + deferredUsers

        val data = allDeferred.awaitAll()
        val listings = data.filterIsInstance<Post>()
        val userResponses = data.filterIsInstance<UserResponse>()

        return@coroutineScope Pair(listings, userResponses)
    }

    fun subscribeToChat(
        chatId: String,
        myId: String,
        myName: String,
        otherName: String,
        otherPfp: String,
    ) {
        fireStoreRepository.subscribeToChat(
            chatId = chatId
        ) {
            // Convert the List<ChatDocument> into a Chat
            // Step 1: Creates a list of Pair<ChatMessageData, String>.
            // The String is the sender's id.
            val messageData = it.map { document ->
                val messageType =
                    if (document.images?.isNotEmpty() == true) {
                        MessageType.Image
                    } else if (document.availabilities != null) {
                        MessageType.Availability
                    } else if (document.startDate != null) {
                        MessageType.State
                    } else {
                        MessageType.Message
                    }

                val meetingInfo = document.startDate?.let {
                    MeetingInfo(
                        proposeTime = it,
                        state = if (document.accepted == true) {
                            "confirmed"
                        } else if (document.accepted == false) {
                            "declined"
                        } else {
                            "proposed"
                        },
                        mostRecent = false
                    )
                }

                val availability = document.availabilities?.let {
                    AvailabilityDocument(
                        availabilities = it.map {
                            AvailabilityBlock(
                                startDate = it.startDate,
                                // lmao wtf is ID
                                id = it.hashCode()
                            )
                        }
                    )
                }

                Pair(
                    ChatMessageData(
                        id = document.id,
                        content = if (meetingInfo != null) {
                            getMeetingInfoContent(
                                meetingInfo = meetingInfo,
                                document = document,
                                myId = myId,
                                otherName = otherName
                            )
                        } else document.text ?: "",
                        timestamp = document.timestamp,
                        senderId = document.senderId,
                        messageType = messageType,
                        imageUrl = document.images?.firstOrNull() ?: "",
                        availability = availability,
                        meetingInfo = meetingInfo
                    ),
                    document.senderId
                )
            }

            // Step 2: Cluster by sender.
            val messageClusters = mutableListOf<ChatMessageCluster>()

            var currentList = mutableListOf<ChatMessageData>()
            var currentSenderId = ""

            messageData.forEach { (message, senderId) ->
                if (senderId != currentSenderId) {
                    if (currentSenderId.isNotEmpty()) {
                        messageClusters.add(
                            ChatMessageCluster(
                                fromUser = currentSenderId == myId,
                                senderId = currentSenderId,
                                messages = currentList.toImmutableList(),
                                senderImage = otherPfp,
                                senderName = if (currentSenderId == myId) myName else otherName
                            )
                        )
                    }
                    currentList = mutableListOf(message)
                    currentSenderId = senderId
                } else {
                    currentList.add(message)
                }
            }

            if (currentSenderId.isNotEmpty()) {
                messageClusters.add(
                    ChatMessageCluster(
                        fromUser = currentSenderId == myId,
                        senderId = currentSenderId,
                        messages = currentList.toImmutableList(),
                        senderImage = otherPfp,
                        senderName = if (currentSenderId == myId) myName else otherName
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
                                senderId = currentSenderId,
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

    /**
     * Marks the most recent message in a chat as read.
     * Only triggers if the most recent message is not already read.
     */
    suspend fun markChatRead(
        chatId: String,
        myId: String,
    ) {
        val mostRecentMessage = fireStoreRepository.getMostRecentMessageId(chatId) ?: ""
        if (fireStoreRepository.getMostRecentMessageRead(chatId, myId)) {
            return
        }
        retrofitInstance.chatApi.markChatRead(
            chatId = chatId,
            messageId = mostRecentMessage,
            markReadBody = MarkReadBody(
                read = true
            )
        )
    }

    private fun getMeetingInfoContent(
        meetingInfo: MeetingInfo,
        document: ChatDocument,
        myId: String,
        otherName: String
    ) = when (meetingInfo.state) {
        "proposed" -> {
            if (document.senderId == myId) {
                "You proposed a new meeting"
            } else {
                "$otherName proposed a new meeting"
            }
        }

        "confirmed" -> {
            if (document.senderId == myId) {
                "You accepted a new meeting"
            } else {
                "$otherName accepted a new meeting"
            }
        }

        "declined" -> {
            if (document.senderId == myId) {
                "You declined the meeting proposal"
            } else {
                "$otherName declined the meeting proposal"
            }
        }

        "canceled" -> {
            if (document.senderId == myId) {
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
        selfIsBuyer: Boolean,
        listingId: String,
        myId: String,
        otherId: String,
        text: String? = null,
        availability: AvailabilityDocument? = null,
        meetingInfo: MeetingInfo? = null,
        imageUrls: List<String>,
        chatId: String,
    ) {
        val buyerId = if (selfIsBuyer) myId else otherId
        val sellerId = if (selfIsBuyer) otherId else myId

        if (text != null) {
            retrofitInstance.chatApi.sendChat(
                chatBody = ChatBody(
                    buyerId = buyerId,
                    sellerId = sellerId,
                    listingId = listingId,
                    text = text,
                    senderId = myId,
                    images = imageUrls,
                ),
                chatId = chatId
            )
        } else if (availability != null) {
            retrofitInstance.chatApi.sendAvailability(
                availabilityBody = AvailabilityBody(
                    buyerId = buyerId,
                    sellerId = sellerId,
                    listingId = listingId,
                    senderId = myId,
                    availabilities = availability.availabilities.map { block ->
                        StartAndEnd(
                            startDate = block.startDate,
                            endDate = block.endDate
                        )
                    }
                ),
                chatId = chatId
            )
        } else if (meetingInfo != null) {

            if (meetingInfo.state == "proposed") {
                retrofitInstance.chatApi.sendProposal(
                    proposalBody = ProposalBody(
                        buyerId = buyerId,
                        sellerId = sellerId,
                        listingId = listingId,
                        senderId = myId,
                        startDate = meetingInfo.proposeTime,
                        endDate = meetingInfo.endTime
                    ),
                    chatId = chatId
                )
            } else if (meetingInfo.state == "confirmed" || meetingInfo.state == "declined") {
                retrofitInstance.chatApi.sendProposalResponse(
                    proposalResponseBody = ProposalResponseBody(
                        buyerId = buyerId,
                        sellerId = sellerId,
                        listingId = listingId,
                        senderId = myId,
                        startDate = meetingInfo.proposeTime,
                        endDate = meetingInfo.endTime,
                        accepted = meetingInfo.state == "confirmed"
                    ),
                    chatId = chatId
                )
            }
        }
    }

    suspend fun sendTextMessage(
        selfIsBuyer: Boolean,
        listingId: String,
        myId: String,
        otherId: String,
        text: String,
        chatId: String,
    ) = sendGenericMessage(
        text = text,
        selfIsBuyer = selfIsBuyer,
        listingId = listingId,
        myId = myId,
        otherId = otherId,
        imageUrls = emptyList(),
        chatId = chatId
    )

    suspend fun sendImageMessage(
        selfIsBuyer: Boolean,
        listingId: String,
        myId: String,
        otherId: String,
        imageBase64: String,
        chatId: String,
    ) {
        val url = retrofitInstance.userApi.uploadImage(
            body = ImageBody(
                imageBase64 = imageBase64
            )
        ).image
        sendGenericMessage(
            selfIsBuyer = selfIsBuyer,
            listingId = listingId,
            myId = myId,
            otherId = otherId,
            imageUrls = listOf(url),
            chatId = chatId,
            text = ""
        )
    }


    suspend fun sendAvailability(
        selfIsBuyer: Boolean,
        listingId: String,
        myId: String,
        otherId: String,
        availability: AvailabilityDocument,
        chatId: String,
    ) = sendGenericMessage(
        selfIsBuyer = selfIsBuyer,
        listingId = listingId,
        myId = myId,
        otherId = otherId,
        imageUrls = listOf(),
        availability = availability,
        chatId = chatId
    )

    suspend fun sendProposalUpdate(
        selfIsBuyer: Boolean,
        listingId: String,
        myId: String,
        otherId: String,
        meetingInfo: MeetingInfo,
        chatId: String,
    ) = sendGenericMessage(
        selfIsBuyer = selfIsBuyer,
        listingId = listingId,
        myId = myId,
        otherId = otherId,
        imageUrls = listOf(),
        meetingInfo = meetingInfo,
        chatId = chatId
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
        otherId: String,
        meetingDate: Date,
    ): Boolean {
        val mapString = dataStore.data.map { preferences ->
            preferences[PreferencesKeys.GCAL_SYNC]
        }.firstOrNull()

        val map = mapString?.let { Json.decodeFromString<Map<String, String>>(mapString) }

        if (map != null && map[otherId] == meetingDate.toString()) {
            return false
        }

        // Store new map, indicating that the user has already been notified for this meeting.
        val newMap = map?.toMutableMap() ?: mutableMapOf()
        newMap[otherId] = meetingDate.toString()
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GCAL_SYNC] = Json.encodeToString(newMap)
        }

        return true
    }
}
