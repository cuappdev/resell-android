package com.cornellappdev.resell.android.viewmodel

import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootNavigationViewModel @Inject constructor(
    val navController: NavHostController,
) : ResellViewModel<Unit>(
    initialUiState = Unit
)
