package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTabBar
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.ProfileViewModel

@Composable
fun ProfileHeader(
    imageUrl: String,
    shopName: String,
    vendorName: String,
    bio: String,
    selectedTab: ProfileViewModel.ProfileTab?,
    onTabSelected: (ProfileViewModel.ProfileTab) -> Unit,
    leftIcon: Int? = null,
    rightIcon: Int? = null,
    onLeftPressed: () -> Unit = {},
    onRightPressed: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ProfilePictureView(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopCenter),
                imageUrl = imageUrl,
            )

            if (leftIcon != null) {
                Icon(
                    painter = painterResource(id = leftIcon),
                    contentDescription = "settings",
                    modifier = Modifier
                        .defaultHorizontalPadding()
                        .padding(top = 8.dp)
                        .size(25.dp)
                        .align(Alignment.TopStart)
                        .clickableNoIndication { onLeftPressed() }
                )
            }

            if (rightIcon != null) {
                Row(
                    modifier = Modifier
                        .defaultHorizontalPadding()
                        .padding(top = 8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = rightIcon),
                        contentDescription = "search",
                        modifier = Modifier
                            .size(25.dp)
                            .clickableNoIndication { onRightPressed() }
                    )
                }
            }
        }

        Text(
            text = shopName,
            modifier = Modifier.padding(top = 12.dp),
            style = Style.heading3
        )

        Text(
            text = vendorName,
            modifier = Modifier.padding(top = 4.dp),
            style = Style.body2,
            color = Secondary
        )

        if (bio.isNotBlank()) {
            Text(
                text = bio,
                modifier = Modifier.padding(top = 12.dp),
                maxLines = 3,
                style = Style.body2,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(20.dp))


        if (selectedTab == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(IconInactive)
            )
        } else {
            ResellTabBar(
                painterIds = listOf(
                    R.drawable.ic_shop,
                    R.drawable.ic_archive,
                    R.drawable.ic_wishlist
                ),
                selectedPainter = selectedTab.ordinal,
            ) {
                onTabSelected(ProfileViewModel.ProfileTab.entries[it])
            }
        }
    }
}

class BioPreviewProvider(
    override val values: Sequence<String> = sequenceOf(
        "This is a short bio.",
        "This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio.This is an insanely super duper very very extremely so long super duper long omg I don't think I've ever seen something this long it is just so long I cannot begin to fathom how long it is bio."
    )
) : PreviewParameterProvider<String>

@Preview
@Composable
fun ProfileHeaderBiosPreview(
    @PreviewParameter(BioPreviewProvider::class) bio: String
) = ResellPreview {
    ProfileHeader(
        imageUrl = "",
        shopName = "Hello",
        vendorName = "world",
        bio = bio,
        selectedTab = null,
        onTabSelected = {}
    )
}
