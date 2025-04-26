package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ResellRootRoute>() {

    fun navigateToPdp(listing: Listing) {
        navigate(
            ResellRootRoute.PDP(
                userImageUrl = listing.user.imageUrl,
                username = listing.user.username,
                userId = listing.user.id,
                userHumanName = listing.user.name,
                listingJson = Json.encodeToString(listing)
            )
        )
    }
}
