package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    mainNavigationRepository: MainNavigationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

    data class MainNavigationUiState(
        val newPostExpanded: Boolean,
        val navEvent: UIEvent<ResellMainScreen>? = null
    )

    init {
        asyncCollect(mainNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    navEvent = route
                )
            }
        }
    }

    fun onNewPostExpandClick() {
        applyMutation {
            copy(
                newPostExpanded = !newPostExpanded
            )
        }
    }

    fun onShadeTapped() {
        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewRequestClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_REQUEST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewPostClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_POST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }
}

@Singleton
class MainNavigationRepository @Inject constructor() : BaseNavigationRepository<ResellMainScreen>()
