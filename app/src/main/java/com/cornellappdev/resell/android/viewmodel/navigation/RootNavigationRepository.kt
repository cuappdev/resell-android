package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ResellRootRoute>() {

    fun navigateToPdp(listing: Listing) {
        navigate(
            ResellRootRoute.PDP(
                id = listing.id,
                title = listing.title,
                price = listing.price,
                images = listing.images,
                description = listing.description,
                categories = listing.categories,
                userImageUrl = listing.user.imageUrl,
                username = listing.user.username,
                userId = listing.user.id,
                userHumanName = listing.user.name
            )
        )
    }
}
