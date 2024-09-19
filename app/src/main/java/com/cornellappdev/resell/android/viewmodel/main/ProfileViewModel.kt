package com.cornellappdev.resell.android.viewmodel.main

import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.LoginRepository
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val navController: NavHostController,
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun onSignOutClick() {
        // TODO: Implement
        loginRepository.invalidateEmail()
        navController.navigate(ResellRootRoute.LOGIN)
    }
}
