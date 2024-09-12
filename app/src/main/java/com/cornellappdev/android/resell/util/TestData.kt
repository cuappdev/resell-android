package com.cornellappdev.android.resell.util

import com.cornellappdev.android.resell.model.Listing

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it,
            title = "title$it",
            image = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
            price = "$$it.00",
            category = "category$it"
        )
    }
}
