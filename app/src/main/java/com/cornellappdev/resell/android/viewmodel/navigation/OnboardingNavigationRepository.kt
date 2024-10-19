package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.onboarding.ResellOnboardingScreen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ResellOnboardingScreen>()
