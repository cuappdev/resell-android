package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.MainNav
import com.cornellappdev.resell.android.model.OnboardingNav
import com.cornellappdev.resell.android.model.RootNav
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingNavigationViewModel @Inject constructor(
    @RootNav val rootNavController: NavHostController,
    @MainNav val mainNavController: NavHostController,
    @OnboardingNav val onboardingNavController: NavHostController,
) : ResellViewModel<Unit>(
    initialUiState = Unit
)
