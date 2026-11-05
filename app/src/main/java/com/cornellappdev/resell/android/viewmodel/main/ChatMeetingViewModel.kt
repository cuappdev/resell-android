package com.cornellappdev.resell.android.viewmodel.main

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.MeetingState
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.confirmedMeeting
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.mostRecentMessage
import com.cornellappdev.resell.android.model.profile.AvailabilityRepository
import com.cornellappdev.resell.android.ui.components.availability.helper.GridSelectionType
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.Style.heading3
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
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
class ChatMeetingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val userInfoRepository: UserInfoRepository,
    private val availabilityRepository: AvailabilityRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    @ApplicationContext private val context: Context
) : ResellViewModel<ChatMeetingViewModel.MeetingUiState>(
    initialUiState = MeetingUiState()
) {
    companion object {
        private const val TAG = "ChatMeetingViewModel"
    }

    data class MeetingUiState(
        val mostRecentOtherAvailability: AvailabilityDocument? = null,
        val confirmedMeeting: MeetingInfo? = null,
    ) {
        val showViewAvailability
            get() = mostRecentOtherAvailability != null
    }

    private val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
    private val listing = Json.decodeFromString<Listing>(navArgs.listingJson)

    private val chatData: Chat?
        get() = chatRepository.subscribedChat(navArgs.chatId)

    private fun onSyncToCalendarPressed(date: Date) {
        val otherName = savedStateHandle.toRoute<ResellRootRoute.CHAT>().name
        val listingName = listing.title

        rootNavigationSheetRepository.hideSheet()

        // Open intent for Google calendar
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

    fun onViewOtherAvailabilityPressed() {
        stateValue().mostRecentOtherAvailability?.let { availability ->
            onAvailabilitySelected(
                availability = availability,
                isSelf = false,
            )
        }
    }

    fun onSendAvailabilityPressed() {
        val canPropose = chatData?.confirmedMeeting() == null

        viewModelScope.launch {
            // Grey out everything but the overlap between both people's saved availability.
            val overlapTimes = try {
                val myId = userInfoRepository.getUserId()
                if (myId == null) {
                    null
                } else {
                    val mine = availabilityRepository.getMyAvailability()
                    val theirs = availabilityRepository.getUserAvailability(navArgs.otherUserId)
                    mine.intersect(theirs).toList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading combined availability: ", e)
                null
            }

            rootNavigationSheetRepository.showBottomSheet(
                sheet = RootSheet.Availability(
                    title = "When are you free to meet?",
                    buttonString = "Propose",
                    description = "Select a 30 minute block",
                    initialButtonState = ResellTextButtonState.DISABLED,
                    callback = {
                        if (canPropose && it.isNotEmpty()) {
                            onMeetingProposal(it.first())
                        } else {
                            rootConfirmationRepository.showError(
                                "Please select a 30-minute block to propose a meeting, and ensure there is no current meeting."
                            )
                        }
                    },
                    gridSelectionType = if (canPropose) GridSelectionType.PROPOSAL else GridSelectionType.NONE,
                    overlapTimes = overlapTimes,
                    onEditAvailability = {
                        rootNavigationSheetRepository.hideSheet()
                        rootNavigationRepository.navigate(ResellRootRoute.AVAILABILITY)
                    }
                )
            )
        }
    }

    fun onAvailabilitySelected(
        availability: AvailabilityDocument,
        isSelf: Boolean,
    ) {
        val canPropose = chatData?.confirmedMeeting() == null

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
                MeetingState.PROPOSED -> {
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

                MeetingState.CONFIRMED -> {
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

                MeetingState.DECLINED -> {
                    val myId = userInfoRepository.getUserId() ?: return@launch
                    val chat = chatData ?: return@launch

                    val mostRecentAvailability = chat.mostRecentMessage {
                        it.availability != null && it.senderId != myId
                    }

                    val availability = mostRecentAvailability?.availability
                    if (availability != null) {
                        onAvailabilitySelected(availability, false)
                    } else {
                        onSendAvailabilityPressed()
                    }
                }

                MeetingState.CANCELED -> {}
            }
        }
    }

    private fun sendMeetingUpdate(
        meetingInfo: MeetingInfo,
        errorMessage: String,
        onSuccess: () -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                chatRepository.sendProposalUpdate(
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = userInfoRepository.getUserId()
                        ?: error("No signed-in user id; cannot send a meeting update"),
                    otherId = navArgs.otherUserId,
                    meetingInfo = meetingInfo,
                    chatId = navArgs.chatId
                )
                onSuccess()
            } catch (e: Exception) {
                rootConfirmationRepository.showError(errorMessage)
                Log.e(TAG, "Error sending ${meetingInfo.state} meeting update: ", e)
            }
        }
    }

    private fun onMeetingProposal(availability: LocalDateTime) {
        rootNavigationSheetRepository.hideSheet()
        sendMeetingUpdate(
            meetingInfo = MeetingInfo(
                state = MeetingState.PROPOSED,
                proposeTime = availability.let {
                    val zoneId = ZoneId.systemDefault()
                    val instant = it.atZone(zoneId).toInstant()
                    val date = Date.from(instant)
                    Timestamp(date)
                },
                mostRecent = false,
            ),
            errorMessage = "Couldn't send your proposal. Please try again."
        )
    }

    private fun onMeetingConfirmed(meetingInfo: MeetingInfo) = sendMeetingUpdate(
        meetingInfo = meetingInfo.copy(state = MeetingState.CONFIRMED),
        errorMessage = "Couldn't confirm the meeting. Please try again.",
        onSuccess = { rootNavigationSheetRepository.hideSheet() }
    )

    private fun onMeetingDeclined(meetingInfo: MeetingInfo) = sendMeetingUpdate(
        meetingInfo = meetingInfo.copy(state = MeetingState.DECLINED),
        errorMessage = "Couldn't decline the proposal. Please try again.",
        onSuccess = { rootNavigationSheetRepository.hideSheet() }
    )

    private fun onMeetingCancelled(meetingInfo: MeetingInfo) = sendMeetingUpdate(
        meetingInfo = meetingInfo.copy(state = MeetingState.CANCELED),
        errorMessage = "Couldn't cancel the meeting. Please try again.",
        onSuccess = { rootNavigationSheetRepository.hideSheet() }
    )

    init {
        val otherId = navArgs.otherUserId

        asyncCollect(chatRepository.subscribedChatFlow(navArgs.chatId)) { response ->
            applyMutation {
                copy(confirmedMeeting = response.asSuccessOrNull()?.data?.confirmedMeeting())
            }

            viewModelScope.launch {
                val confirmedMeetingInfo = chatData?.confirmedMeeting()
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
                    val myId = userInfoRepository.getUserId() ?: return@launch
                    val data = chatData?.mostRecentMessage {
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
