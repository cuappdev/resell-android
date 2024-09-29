package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.Listing

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it,
            title = "title$it",
            imageUrl = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
            price = "$$it.00",
            category = "category$it"
        )
    }
}

val fakeRichieListings = { count: Int ->
    List(count) {
        Listing(
            imageUrl = "https://kohantextilejournal.com/wp-content/uploads/2020/11/Mr.-Ruizhe-Sun-China-768x384.jpg",
            title = "title$it",
            price = "$$it.00",
            category = "category$it",
            id = it,
        )
    }
}

val tallRichieListings = { count: Int ->
    List(count) {
        Listing(
            imageUrl = "https://images.squarespace-cdn.com/content/v1/558a10ebe4b09c778262786f/1557230577805-U5SJ6EPAWZMYGGTDEAWY/Richie-Moriarty-B46A2378-20.jpg",
            title = "title$it",
            price = "$$it.00",
            category = "category$it",
            id = it,
        )
    }
}
