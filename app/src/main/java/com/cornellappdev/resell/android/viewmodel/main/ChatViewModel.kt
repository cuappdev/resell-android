package com.cornellappdev.resell.android.viewmodel.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityBlock
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        val sellerName: String = "Unknown",
        val title: String = "Unknown",
        val typedMessage: String = "",
        val scrollBottom: UIEvent<Unit>? = null,
        val listing: Listing? = null
    ) {
        val showNegotiate
            get() = true

        val showPayWithVenmo
            get() = chatType == ChatType.Purchases

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

    enum class ChatType {
        Purchases, Offers
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onMeetingDetailsPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Cancel Meeting",
                title = "Meeting Details",
                closeString = "Close",
                confirmColor = ResellTextButtonContainer.PRIMARY_RED
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Meeting with Lia for Blue Pants confirmed for",
                    style = Style.title4
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Time",
                    style = Style.title3
                )
                Text(
                    text = "Friday, October 23 - 1:30-2:00 PM",
                    style = Style.title4
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "For safety, make sure to meet up in a public space on campus",
                    style = Style.title4
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onProposalDetailsPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Edit Proposal",
                title = "Proposal Details",
                closeString = "Cancel",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "You have proposed the following meeting:",
                    style = Style.body1
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Time",
                    style = Style.title3
                )
                Text(
                    text = "Friday, October 23 - 1:30-2:00 PM",
                    style = Style.body1
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onSyncToCalendarPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Sync",
                title = "Sync to Google Calendar?",
                closeString = "Close",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "A new meeting has been detected would you like to sync your calendar?",
                    style = Style.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }
        )
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
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
        val listing = Json.decodeFromString<Listing>(navArgs.postJson)

        applyMutation {
            copy(
                typedMessage = ""
            )
        }
        viewModelScope.launch {
            val myInfo = userInfoRepository.getUserInfo()
            try {
                chatRepository.sendTextMessage(
                    myEmail = myInfo.email,
                    otherEmail = navArgs.email,
                    text = message,
                    selfIsBuyer = navArgs.isBuyer,
                    postId = listing.id,
                    myName = myInfo.name,
                    otherName = navArgs.name,
                    myImageUrl = myInfo.imageUrl,
                    otherImageUrl = navArgs.pfp
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
                defaultPrice = stateValue().listing!!.price.replace("$", ""),
                callback = { price ->
                    if (stateValue().chatType == ChatType.Purchases) {
                        onTyped(
                            message = "Hi! I'm interested in buying your ${stateValue().listing!!.title}, but would you be open to selling it for $$price?"
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
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
        val listing = Json.decodeFromString<Listing>(navArgs.postJson)

        viewModelScope.launch {
            try {
                val myInfo = userInfoRepository.getUserInfo()

                val asTimeStamp = availability.map {
                    it.convertToFirestoreTimestamp()
                }

                chatRepository.sendAvailability(
                    myEmail = myInfo.email,
                    otherEmail = navArgs.email,
                    selfIsBuyer = navArgs.isBuyer,
                    postId = listing.id,
                    myName = myInfo.name,
                    otherName = navArgs.name,
                    myImageUrl = myInfo.imageUrl,
                    otherImageUrl = navArgs.pfp,
                    availability = AvailabilityDocument(
                        asTimeStamp.mapIndexed { index, it ->
                            AvailabilityBlock(
                                startDate = it,
                                id = index
                            )
                        }
                    )
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
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()

        try {
            val theirVenmo = fireStoreRepository.getVenmoHandle(
                email = navArgs.email
            )

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
                    description = "The user has not set up venmo yet, please contact the user directly.",
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
                    val listing = stateValue().listing!!
                    val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
                    try {
                        chatRepository.sendImageMessage(
                            myEmail = myInfo.email,
                            otherEmail = navArgs.email,
                            selfIsBuyer = navArgs.isBuyer,
                            postId = listing.id,
                            myName = myInfo.name,
                            otherName = navArgs.name,
                            myImageUrl = myInfo.imageUrl,
                            otherImageUrl = navArgs.pfp,
                            imageBase64 = bitmap.toNetworkingString()
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

    fun onAvailabilitySelected(
        availability: AvailabilityDocument,
        isSelf: Boolean,
    ) {
        // TODO: Derive correctly based on if there's already been a confirmed meeting
        val chat = chatRepository.subscribedChatFlow.value.asSuccessOrNull()!!.data
        val mostRecentState = chat.chatHistory.map {
            it.messages
        }.flatten().sortedByDescending {
            it.timestamp
        }.firstOrNull {
            it.meetingInfo != null
        }

        val canPropose =
            !(mostRecentState != null && mostRecentState.meetingInfo!!.state == "confirmed")

        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
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
        val listingName = stateValue().listing?.title ?: ""
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
                                append("Meeting with $otherName for $listingName confirmed for:\n\n")
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
                        chatRepository.subscribedChatFlow.value.asSuccessOrNull()?.data ?: return@launch

                    val mostRecentAvailability = chat.chatHistory.map {
                        it.messages
                    }.flatten().sortedByDescending {
                        it.timestamp
                    }.firstOrNull {
                        it.availability != null && it.senderEmail != myEmail
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
                    myEmail = userInfoRepository.getUserInfo().email,
                    otherEmail = savedStateHandle.toRoute<ResellRootRoute.CHAT>().email,
                    selfIsBuyer = savedStateHandle.toRoute<ResellRootRoute.CHAT>().isBuyer,
                    postId = stateValue().listing?.id ?: "",
                    myName = userInfoRepository.getUserInfo().name,
                    otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name,
                    myImageUrl = userInfoRepository.getUserInfo().imageUrl,
                    otherImageUrl = savedStateHandle.toRoute<ResellRootRoute.CHAT>().pfp,
                    meetingInfo = MeetingInfo(
                        state = "proposed",
                        proposer = userInfoRepository.getUserInfo().email,
                        proposeTime = availability.let {
                            val formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy, h:mm a")
                            // Format the LocalDateTime object
                            it.format(formatter)
                        },
                        canceler = null,
                        mostRecent = false,
                    )
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
                    myEmail = userInfoRepository.getUserInfo().email,
                    otherEmail = savedStateHandle.toRoute<ResellRootRoute.CHAT>().email,
                    selfIsBuyer = savedStateHandle.toRoute<ResellRootRoute.CHAT>().isBuyer,
                    postId = stateValue().listing?.id ?: "",
                    myName = userInfoRepository.getUserInfo().name,
                    otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name,
                    myImageUrl = userInfoRepository.getUserInfo().imageUrl,
                    otherImageUrl = savedStateHandle.toRoute<ResellRootRoute.CHAT>().pfp,
                    meetingInfo = meetingInfo.copy(
                        state = "confirmed",
                        proposer = null,
                        canceler = null,
                    )
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
                    myEmail = userInfoRepository.getUserInfo().email,
                    otherEmail = savedStateHandle.toRoute<ResellRootRoute.CHAT>().email,
                    selfIsBuyer = savedStateHandle.toRoute<ResellRootRoute.CHAT>().isBuyer,
                    postId = stateValue().listing?.id ?: "",
                    myName = userInfoRepository.getUserInfo().name,
                    otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name,
                    myImageUrl = userInfoRepository.getUserInfo().imageUrl,
                    otherImageUrl = savedStateHandle.toRoute<ResellRootRoute.CHAT>().pfp,
                    meetingInfo = meetingInfo.copy(
                        state = "declined",
                        proposer = null,
                        canceler = null,
                    )
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
                    myEmail = userInfoRepository.getUserInfo().email,
                    otherEmail = savedStateHandle.toRoute<ResellRootRoute.CHAT>().email,
                    selfIsBuyer = savedStateHandle.toRoute<ResellRootRoute.CHAT>().isBuyer,
                    postId = stateValue().listing?.id ?: "",
                    myName = userInfoRepository.getUserInfo().name,
                    otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name,
                    myImageUrl = userInfoRepository.getUserInfo().imageUrl,
                    otherImageUrl = savedStateHandle.toRoute<ResellRootRoute.CHAT>().pfp,
                    meetingInfo = meetingInfo.copy(
                        state = "canceled",
                        proposer = null,
                        canceler = userInfoRepository.getEmail(),
                    )
                )
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("ChatViewModel", "onMeetingCancelled: ", e)
            }
        }
    }

    init {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
        val listing = Json.decodeFromString<Listing>(navArgs.postJson)

        applyMutation {
            copy(
                listing = listing,
            )
        }

        firebaseMessagingRepository.requestNotificationsPermission()

        applyMutation {
            copy(
                sellerName = navArgs.name,
                title = listing.title,
                chatType = if (navArgs.isBuyer) ChatType.Purchases else ChatType.Offers,
            )
        }

        viewModelScope.launch {
            val myEmail = userInfoRepository.getEmail()!!
            val theirEmail = navArgs.email

            chatRepository.subscribeToChat(
                myEmail = myEmail,
                otherEmail = theirEmail,
                selfIsBuyer = navArgs.isBuyer,
                myName = userInfoRepository.getFirstName()!!,
                otherName = navArgs.name
            )
        }

        asyncCollect(chatRepository.subscribedChatFlow) { response ->
            applyMutation {
                copy(
                    currentChat = response,
                    scrollBottom = UIEvent(Unit)
                )
            }
        }
    }
}
