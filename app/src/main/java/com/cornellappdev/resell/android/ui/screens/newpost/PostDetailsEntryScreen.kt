package com.cornellappdev.resell.android.ui.screens.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTag
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.components.newpost.MoneyEntry
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import com.cornellappdev.resell.android.viewmodel.newpost.PostDetailsEntryViewModel

@Composable
fun PostDetailsEntryScreen(
    postDetailsEntryViewModel: PostDetailsEntryViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PostDetailsContent(
            onPricePressed = postDetailsEntryViewModel::onPricePressed
        )

        ResellTextButton(
            text = "Next",
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp)
        )
    }
}

@Preview
@Composable
private fun PostDetailsContent(
    onPricePressed: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = "New Listing",
        )

        Spacer(Modifier.height(24.dp))

        ResellTextEntry(
            label = "Title",
            text = "",
            onTextChange = {},
            inlineLabel = false,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(32.dp))

        MoneyEntry(
            text = "",
            label = "Price",
            onPressed = onPricePressed,
            modifier = Modifier
                .defaultHorizontalPadding()
                .align(Alignment.Start)
        )

        Spacer(Modifier.height(32.dp))

        ResellTextEntry(
            label = "Item Description",
            text = "",
            onTextChange = {},
            inlineLabel = false,
            multiLineHeight = 255.dp,
            singleLine = false,
            // TODO: probably wrong max lines
            maxLines = 3,
            modifier = Modifier.defaultHorizontalPadding(),
            placeholder = "enter item details..."
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Select Categories",
            style = Style.title1,
            modifier = Modifier
                .padding(start = 24.dp, bottom = 8.dp)
                .align(Alignment.Start)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Padding.medium, Alignment.Start),
        ) {
            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }

            // TODO Not correct filters
            items(items = HomeViewModel.HomeFilter.entries) { filter ->
                ResellTag(
                    text = filter.name.lowercase().replaceFirstChar {
                        it.uppercase()
                    },
                    active = false,
                    onClick = { /* TODO */ }
                )
            }

            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}
