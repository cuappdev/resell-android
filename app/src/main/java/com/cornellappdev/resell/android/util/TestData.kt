package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.UserInfo

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it.toString(),
            title = "title$it",
            images = listOf(richieUrl),
            price = "$$it.00",
            categories = listOf("category$it"),
            description = "Hello! I need to sell this.",
            user = richieUserInfo
        )
    }
}

val richieUrl =
    "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc"

val tallUrl =
    "https://preview.redd.it/rmcg6gmzpa221.jpg?width=640&crop=smart&auto=webp&s=fae5cce02314436e1407cff714894dde0e40ddab"

val longUrl = "https://i.imgflip.com/6o9hva.jpg"

val richieUserInfo = UserInfo(
    name = "Richie",
    imageUrl = richieUrl,
    netId = "richie",
    username = "richie",
    venmoHandle = "richie",
    bio = "bio bio bio",
    id = "richie"
)
