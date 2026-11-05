package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.global.ResellStarRating
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.LocalInfiniteShimmer
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun ExternalProfileHeader(
    imageUrl: String,
    name: String,
    averageRating: Double,
    reviewCount: Int,
    bio: String,
    modifier: Modifier = Modifier,
    showRating: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp)
            .defaultHorizontalPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfilePictureView(
                imageUrl = imageUrl,
                modifier = Modifier.size(64.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = name, style = Style.heading2)

                Row(
                    modifier = Modifier
                        .alpha(if (showRating) 1f else 0f)
                        .then(if (showRating) Modifier else Modifier.clearAndSetSemantics {}),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResellStarRating(rating = averageRating, starSize = 12.dp)
                    Text(
                        text = "($reviewCount)",
                        style = Style.body2,
                        color = IconInactive,
                    )
                }
            }
        }

        if (bio.isNotBlank()) {
            Text(text = bio, style = Style.body2)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExternalProfileHeaderPreview() = ResellPreview {
    CompositionLocalProvider(value = LocalInfiniteShimmer provides Color.LightGray) {
        ExternalProfileHeader(
            imageUrl = "",
            name = "Ravina",
            averageRating = 4.3,
            reviewCount = 12,
            bio = "Hi, I'm Ravina! Looking for great deals and selling even greater items."
        )
    }
}
