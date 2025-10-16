package com.cornellappdev.resell.android.viewmodel.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityBlock
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.TransactionInfo
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.ui.components.availability.helper.GridSelectionType
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Style.heading3
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.convertToFirestoreTimestamp
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.util.toNetworkingString
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val userInfoRepository: UserInfoRepository,
    private val chatRepository: ChatRepository,
    private val savedStateHandle: SavedStateHandle,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    @ApplicationContext private val context: Context
) :
    ResellViewModel<ChatViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chatType = ChatType.Purchases,
            currentChat = ResellApiResponse.Pending
        )
    ) {
    data class MessagesUiState(
        val chatType: ChatType,
        val currentChat: ResellApiResponse<Chat>,
        val otherName: String = "Unknown",
        val title: String = "Unknown",
        val typedMessage: String = "",
        val scrollBottom: UIEvent<Unit>? = null,
        val mostRecentOtherAvailability: AvailabilityDocument? = null,
    ) {
        val showNegotiate
            get() = true

        val showPayWithVenmo
            get() = chatType == ChatType.Purchases

        val showViewAvailability
            get() = mostRecentOtherAvailability != null

        val confirmedMeeting: MeetingInfo?
            get() = currentChat.asSuccessOrNull()?.data?.let { chat ->
                val mostRecentState = chat.chatHistory.map {
                    it.messages
                }.flatten().sortedByDescending {
                    it.timestamp
                }.firstOrNull {
                    it.meetingInfo != null
                }

                if (mostRecentState != null && mostRecentState.meetingInfo!!.state == "confirmed") {
                    mostRecentState.meetingInfo
                } else {
                    null
                }
            }
    }

    val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
    val listing = Json.decodeFromString<Listing>(navArgs.listingJson)

    enum class ChatType {
        Purchases, Offers
    }

    fun onPTFClicked() {
        navController.navigate(
            ResellRootRoute.POST_TRANSACTION_RATING
        )
    }
    fun onBackPressed() {
        navController.popBackStack()
    }

    private fun onSyncToCalendarPressed(date: Date) {
        val otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name
        val listingName = listing.title

        rootNavigationSheetRepository.hideSheet()

        // Open intent for google calendar
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
            // 30 minutes
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time + 30 * 60 * 1000)
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(
                CalendarContract.Events.TITLE, "Meeting with $otherName ${
                    if (listingName.isNotEmpty()) {
                        "for $listingName"
                    } else ""
                }"
            )

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    fun onNewProposalPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Reschedule",
                title = "New Proposal",
                closeString = "Cancel",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "This proposal replaces your previous one scheduled for [Original Time]. Are you sure you want to reschedule?",
                    style = Style.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onSendMessage(message: String) {
        applyMutation {
            copy(
                typedMessage = ""
            )
        }
        viewModelScope.launch {
            val myInfo = userInfoRepository.getUserInfo()
            try {
                chatRepository.sendTextMessage(
                    text = message,
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = myInfo.id,
                    otherId = navArgs.otherUserId,
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ", e)
                rootConfirmationRepository.showError(
                    "Something went wrong while sending your message."
                )
            }
        }
    }

    fun onPostClicked(post: Post) {
        rootNavigationRepository.navigateToPdp(
            post.toListing()
        )
    }

    fun onTyped(message: String) = applyMutation { copy(typedMessage = message) }

    fun onNegotiatePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                confirmString = "Propose",
                title = "What price do you want to propose?",
                defaultPrice = listing.price.replace("$", ""),
                callback = { price ->
                    if (stateValue().chatType == ChatType.Purchases) {
                        onTyped(
                            message = "Hi! I'm interested in buying your ${listing.title}, but would you be open to selling it for $$price?"
                        )
                    } else {
                        onTyped(
                            message = "The lowest I can accept for this item would be $$price."
                        )
                    }
                }
            )
        )
    }

    fun onViewOtherAvailabilityPressed() {
        stateValue().mostRecentOtherAvailability?.let { availability ->
            onAvailabilitySelected(
                availability = availability,
                isSelf = false,
            )
        }
    }

    fun onSendAvailabilityPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            sheet = RootSheet.Availability(
                title = "When are you free to meet?",
                buttonString = "Continue",
                description = "Drag across the grid to add/remove availability",
                callback = ::availabilityCallback,
                gridSelectionType = GridSelectionType.AVAILABILITY
            )
        )
    }

    private fun availabilityCallback(availability: List<LocalDateTime>) {
        viewModelScope.launch {
            try {
                val myInfo = userInfoRepository.getUserInfo()

                val asTimeStamp = availability.map {
                    it.convertToFirestoreTimestamp()
                }

                chatRepository.sendAvailability(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = myInfo.id,
                    otherId = navArgs.otherUserId,
                    availability = AvailabilityDocument(
                        asTimeStamp.mapIndexed { index, it ->
                            AvailabilityBlock(
                                startDate = it,
                                id = index
                            )
                        }
                    ),
                    chatId = navArgs.chatId
                )
                rootNavigationSheetRepository.hideSheet()
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending availability: ", e)
                rootConfirmationRepository.showError(
                    "Something went wrong while sending your availability. Please try again later."
                )
            }
        }
    }

    fun payWithVenmoPressed() = viewModelScope.launch {
        try {
            val theirVenmo = navArgs.otherVenmo
            if (theirVenmo.isNotBlank()) {
                // Open Venmo URL
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://account.venmo.com/u/${theirVenmo}")
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                throw Exception("No Venmo handle found")
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error getting Venmo handle: ", e)
            rootDialogRepository.showDialog(
                RootDialogContent.TwoButtonDialog(
                    title = "Venmo Not Set Up",
                    description = "${stateValue().otherName} has not set up their venmo yet.\nPlease contact them directly.",
                    primaryButtonText = "Dismiss",
                    secondaryButtonText = null,
                    onPrimaryButtonClick = {
                        rootDialogRepository.dismissDialog()
                    },
                    onSecondaryButtonClick = {},
                    exitButton = true
                )
            )
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(context, uri)?.asImageBitmap()
            if (bitmap != null) {
                viewModelScope.launch {
                    val myInfo = userInfoRepository.getUserInfo()
                    try {
                        chatRepository.sendImageMessage(
                            selfIsBuyer = navArgs.isBuyer,
                            listingId = listing.id,
                            myId = myInfo.id,
                            otherId = navArgs.otherUserId,
                            imageBase64 = bitmap.toNetworkingString(),
                            chatId = navArgs.chatId
                        )
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "Error sending message: ", e)
                        rootConfirmationRepository.showError(
                            "Something went wrong while sending your image."
                        )
                    }
                }
            } else {
                rootConfirmationRepository.showError(
                    "Could not load image. Please try again."
                )
            }
        }
    }

    /**
     * Returns the most recent [ChatMessageData] that matches the given [predicate], or null if
     * no chat matching the predicate is found.
     */
    private fun getFirstChatOrNull(predicate: (ChatMessageData) -> Boolean): ChatMessageData? {
        val chat = chatRepository.subscribedChatFlow.value.asSuccessOrNull()?.data ?: return null
        val mostRecentState = chat.chatHistory.map {
            it.messages
        }.flatten().sortedByDescending {
            it.timestamp
        }.firstOrNull {
            predicate(it)
        }

        return mostRecentState
    }

    private fun mostRecentMeetingStateIs(state: String): MeetingInfo? {
        val mostRecentState = getFirstChatOrNull {
            it.meetingInfo != null
        }

        // If the most recent state is the one we're looking for, return it.
        if (mostRecentState != null && mostRecentState.meetingInfo!!.state == state)
            return mostRecentState.meetingInfo

        return null
    }

    fun onAvailabilitySelected(
        availability: AvailabilityDocument,
        isSelf: Boolean,
    ) {
        val canPropose = mostRecentMeetingStateIs("confirmed") == null

        rootNavigationSheetRepository.showBottomSheet(
            sheet = RootSheet.Availability(
                title = if (isSelf) "Your Availability" else "${navArgs.name}'s Availability",
                buttonString = if (canPropose) "Propose" else "Continue",
                description = if (isSelf) {
                    "You previously proposed this availability"
                } else if (canPropose) {
                    "Select a 30-minute block to propose a meeting"
                } else {
                    "${navArgs.name} previously proposed this availability"
                },
                callback = {
                    if (!isSelf && canPropose && it.isNotEmpty()) {
                        onMeetingProposal(it.first())
                    } else {
                        rootConfirmationRepository.showError(
                            "Please select a 30-minute block to propose a meeting, and ensure there is no current meeting."
                        )
                    }
                },
                initialTimes = availability.availabilities.map {
                    val date = it.startDate.toDate()
                    // Convert Date to LocalDateTime
                    LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                },
                initialButtonState = ResellTextButtonState.DISABLED,
                gridSelectionType = if (!isSelf && canPropose) {
                    GridSelectionType.PROPOSAL
                } else {
                    GridSelectionType.NONE
                }
            )
        )
    }

    fun onMeetingStateClicked(meetingInfo: MeetingInfo, isSelf: Boolean) {
        val name = if (isSelf) "You" else savedStateHandle.toRoute<ResellRootRoute.CHAT>().name
        val otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name
        viewModelScope.launch {
            when (meetingInfo.state) {
                "proposed" -> {
                    rootNavigationSheetRepository.showBottomSheet(
                        RootSheet.TwoButtonSheet(
                            title = "Proposal Details",
                            description = buildAnnotatedString {
                                append("$name proposed the following meeting time:\n\n")
                                withStyle(style = heading3.toSpanStyle()) {
                                    append("Time:\n")
                                }
                                append(meetingInfo.convertToMeetingString())
                            },
                            primaryText = "Confirm",
                            secondaryText = "Decline",
                            primaryCallback = { onMeetingConfirmed(meetingInfo) },
                            secondaryCallback = {
                                onMeetingDeclined(meetingInfo)
                            },
                            primaryButtonState = if (isSelf) {
                                ResellTextButtonState.DISABLED
                            } else {
                                ResellTextButtonState.ENABLED
                            },
                            secondaryButtonState = if (isSelf) {
                                ResellTextButtonState.DISABLED
                            } else {
                                ResellTextButtonState.ENABLED
                            },
                            secondaryContainerType = ResellTextButtonContainer.SECONDARY
                        )
                    )
                }

                "confirmed" -> {
                    rootNavigationSheetRepository.showBottomSheet(
                        RootSheet.TwoButtonSheet(
                            title = "Meeting Details",
                            description = buildAnnotatedString {
                                append("Meeting with $otherName for ${listing.title} confirmed for:\n\n")
                                withStyle(style = heading3.toSpanStyle()) {
                                    append("Time:\n")
                                }
                                append(meetingInfo.convertToMeetingString())
                            },
                            primaryText = "Cancel Meeting",
                            secondaryText = "Close",
                            primaryContainerType = ResellTextButtonContainer.PRIMARY_RED,
                            secondaryContainerType = ResellTextButtonContainer.NAKED,
                            primaryCallback = {
                                onMeetingCancelled(meetingInfo)
                            },
                            secondaryCallback = {
                                rootNavigationSheetRepository.hideSheet()
                            },
                        )
                    )
                }

                "declined" -> {
                    val myEmail = userInfoRepository.getUserInfo().email
                    val chat =
                        chatRepository.subscribedChatFlow.value.asSuccessOrNull()?.data
                            ?: return@launch

                    val mostRecentAvailability = chat.chatHistory.map {
                        it.messages
                    }.flatten().sortedByDescending {
                        it.timestamp
                    }.firstOrNull {
                        it.availability != null && it.senderId != myEmail
                    }

                    mostRecentAvailability?.availability?.let {
                        onAvailabilitySelected(it, false)
                    }
                }

                "canceled" -> {}

                else -> {}
            }
        }
    }

    private fun onMeetingProposal(availability: LocalDateTime) {
        rootNavigationSheetRepository.hideSheet()
        viewModelScope.launch {
            try {
                chatRepository.sendProposalUpdate(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = userInfoRepository.getUserId() ?: "",
                    otherId = navArgs.otherUserId,
                    meetingInfo = MeetingInfo(
                        state = "proposed",
                        proposeTime = availability.let {
                            val zoneId = ZoneId.systemDefault()
                            val instant = it.atZone(zoneId).toInstant()
                            val date = Date.from(instant)
                            Timestamp(date)
                        },
                        mostRecent = false,
                    ),
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("ChatViewModel", "onMeetingProposal: ", e)
            }
        }
    }

    private fun onMeetingConfirmed(meetingInfo: MeetingInfo) {
        rootNavigationSheetRepository.hideSheet()
        viewModelScope.launch {
            try {
                chatRepository.sendProposalUpdate(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = userInfoRepository.getUserId() ?: "",
                    otherId = navArgs.otherUserId,
                    meetingInfo = meetingInfo.copy(
                        state = "confirmed",
                    ),
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("ChatViewModel", "onMeetingConfirmed: ", e)
            }
        }
    }

    private fun onMeetingDeclined(meetingInfo: MeetingInfo) {
        rootNavigationSheetRepository.hideSheet()
        viewModelScope.launch {
            try {
                chatRepository.sendProposalUpdate(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = userInfoRepository.getUserId() ?: "",
                    otherId = navArgs.otherUserId,
                    meetingInfo = meetingInfo.copy(
                        state = "declined",
                    ),
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("ChatViewModel", "onMeetingDeclined: ", e)
            }
        }
    }

    private fun onMeetingCancelled(meetingInfo: MeetingInfo) {
        rootNavigationSheetRepository.hideSheet()
        viewModelScope.launch {
            try {
                chatRepository.sendProposalUpdate(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = userInfoRepository.getUserId() ?: "",
                    otherId = navArgs.otherUserId,
                    meetingInfo = meetingInfo.copy(
                        state = "canceled",
                    ),
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("ChatViewModel", "onMeetingCancelled: ", e)
            }
        }
    }

    fun onTransactionStateClicked(transactionInfo: TransactionInfo) {
        viewModelScope.launch {
            when (transactionInfo.state) {
                "completed" -> {
                    rootNavigationRepository.navigate(ResellRootRoute.POST_TRANSACTION_RATING)
                }
            }
        }
    }

    init {
        firebaseMessagingRepository.requestNotificationsPermission()

        val title = listing.title
        val otherId = navArgs.otherUserId
        applyMutation {
            copy(
                otherName = navArgs.name,
                title = title,
                chatType = if (navArgs.isBuyer) ChatType.Purchases else ChatType.Offers,
            )
        }

        viewModelScope.launch {
            val myId = userInfoRepository.getUserId() ?: ""
            chatRepository.subscribeToChat(
                myName = userInfoRepository.getFirstName()!!,
                otherName = navArgs.name,
                chatId = navArgs.chatId,
                myId = myId,
                otherPfp = navArgs.pfp
            )

            chatRepository.markChatRead(
                chatId = navArgs.chatId,
                myId = myId
            )
        }

        asyncCollect(chatRepository.subscribedChatFlow) { response ->
            applyMutation {
                copy(
                    currentChat = response,
                    scrollBottom = UIEvent(Unit)
                )
            }

            viewModelScope.launch {
                val confirmedMeetingInfo = mostRecentMeetingStateIs("confirmed")
                if (response is ResellApiResponse.Success
                    && confirmedMeetingInfo != null
                    && chatRepository.shouldShowGCalSync(
                        otherId = otherId,
                        meetingDate = confirmedMeetingInfo.convertToUtcMinusFiveDate()
                    )
                ) {
                    rootNavigationSheetRepository.showBottomSheet(
                        RootSheet.TwoButtonSheet(
                            title = "Sync to Google Calendar?",
                            description = AnnotatedString("A new meeting has been detected, would you like to sync to your calendar?"),
                            primaryText = "Sync",
                            secondaryText = "Close",
                            primaryCallback = {
                                onSyncToCalendarPressed(confirmedMeetingInfo.convertToUtcMinusFiveDate())
                            },
                            secondaryCallback = {
                                rootNavigationSheetRepository.hideSheet()
                            },
                            secondaryContainerType = ResellTextButtonContainer.NAKED
                        )
                    )
                }
            }

            // If chat has an availability message from the other user, show that tab.
            viewModelScope.launch {
                if (response is ResellApiResponse.Success) {
                    val myId = userInfoRepository.getUserId()!!
                    val data = getFirstChatOrNull {
                        it.availability != null && it.senderId != myId
                    }

                    applyMutation {
                        copy(
                            mostRecentOtherAvailability = data?.availability
                        )
                    }
                }
            }
        }
    }
}
