package com.cornellappdev.resell.android.ui.components.chat.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.util.LocalInfiniteShimmer

@Composable
fun MessageCardLoading(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            ProfilePictureView(
                imageUrl = "",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier.height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadingBlob(modifier = Modifier
                    .height(height = 20.dp)
                    .weight(1f))
            }
            Spacer(modifier = Modifier.height(1.dp))
            LoadingBlob(modifier = Modifier.size(width = 100.dp, height = 20.dp))
        }
    }
}

@Composable
private fun LoadingBlob(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = LocalInfiniteShimmer.current
    ) {}
}

@Preview
@Composable
private fun MessageCardLoadingPreview() = ResellPreview {
    MessageCardLoading()
}
