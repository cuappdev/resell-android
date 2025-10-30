package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.feedback.FeedbackScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class FeedbackNavigationViewModel @Inject constructor(
    val feedbackNavigationRepository: FeedbackNavigationRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<FeedbackNavigationViewModel.UiState>(
        initialUiState = UiState(
            route = null,
            initialPage = FeedbackScreen.Reason(
                postId = savedStateHandle.toRoute<ResellRootRoute.FEEDBACK>().postId,
                userId = savedStateHandle.toRoute<ResellRootRoute.FEEDBACK>().userId,
                userName = savedStateHandle.toRoute<ResellRootRoute.FEEDBACK>().userName
            )
        ),
    ) {
    data class UiState(
        val route: UIEvent<FeedbackScreen>?,
        val popBackStack: UIEvent<Unit>? = null,
        val initialPage: FeedbackScreen?
    )

    init {
        asyncCollect(feedbackNavigationRepository.routeFlow) { route ->
            feedbackNavigationRepository.setCanNavigate(false)
            applyMutation {
                copy(
                    route = route
                )
            }
        }

        asyncCollect(feedbackNavigationRepository.popBackStackFlow) { pop ->
            feedbackNavigationRepository.setCanNavigate(false)
            applyMutation {
                copy(
                    popBackStack = pop
                )
            }
        }
    }
}

@Singleton
class FeedbackNavigationRepository @Inject constructor() :
    BaseNavigationRepository<FeedbackScreen>() {
        private val _canNavigateFlow = MutableStateFlow(true)
        val canNavigateFlow: StateFlow<Boolean> = _canNavigateFlow.asStateFlow()

        fun setCanNavigate(canNavigate: Boolean) {
            _canNavigateFlow.value = canNavigate
        }
    }
