package com.cornellappdev.resell.android.ui.components.main

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.components.global.ResellCard
import com.cornellappdev.resell.android.ui.components.global.ResellCardContent
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun FromHistoryBody(
    modifier: Modifier,
    categories: List<Pair<String, List<Listing>>>,
    onListingPressed: (Listing) -> Unit,
    onHidePressed: (String) -> Unit
) {


    val isPreview = LocalInspectionMode.current
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 100.dp, top = 12.dp),
        verticalItemSpacing = 24.dp,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = category.first, style = Style.heading3)
                        Text(
                            text = "Hide",
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                onHidePressed(category.first)
                            }
                        )
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(category.second) { item ->
                            if (isPreview) {
                                ResellCardContent(
                                    onClick = { },
                                    image = ResellApiResponse.Success(drawableResToImageBitmap(R.drawable.ic_appdev)),
                                    title = "",
                                    price = ""
                                )
                            } else {


                                ResellCard(
                                    imageUrl = item.image,
                                    title = item.title,
                                    price = item.price,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    onListingPressed(item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun drawableResToImageBitmap(@DrawableRes resId: Int): ImageBitmap {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, resId)
    val bitmap: Bitmap = if (drawable == null) {
        createBitmap(1, 1)
    } else {
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
        val bmp = createBitmap(width, height)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bmp
    }
    return bitmap.asImageBitmap()
}

@Preview
@Composable
private fun FromHistoryBodyPreview() {
    FromHistoryBody(
        Modifier, listOf(
            Pair(
                "Books",
                listOf(
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                )
            ),
            Pair(
                "Clothes",
                listOf(
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                )
            ),
            Pair(
                "Pink",
                listOf(
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                    Listing(
                        id = "1",
                        title = "The Fault in Our Stars",
                        images = listOf(),
                        price = "$20",
                        categories = listOf("Books", "Fantasy"),
                        description = "Blah Blah Blah",
                        user = UserInfo(
                            username = "temp",
                            name = "temp",
                            netId = "temp",
                            venmoHandle = "temp",
                            bio = "temp",
                            imageUrl = "temp",
                            id = "temp",
                            email = "temp"
                        )
                    ),
                )
            )
        ), {}, { }
    )
}

