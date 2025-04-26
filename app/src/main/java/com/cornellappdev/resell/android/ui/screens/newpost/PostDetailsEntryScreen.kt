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
    val uiState = postDetailsEntryViewModel.collectUiStateValue()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PostDetailsContent(
            uiState = uiState,
            onPricePressed = postDetailsEntryViewModel::onPricePressed,
            onDescriptionChanged = postDetailsEntryViewModel::onDescriptionChanged,
            onTitleChanged = postDetailsEntryViewModel::onTitleChanged,
            onHomeFilterPressed = postDetailsEntryViewModel::onFilterPressed
        )

        ResellTextButton(
            text = "Next",
            onClick = postDetailsEntryViewModel::onConfirmPost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp),
            state = uiState.buttonState,
        )
    }
}

@Preview
@Composable
private fun PostDetailsContent(
    uiState: PostDetailsEntryViewModel.PostEntryUiState =
        PostDetailsEntryViewModel.PostEntryUiState(),
    onPricePressed: () -> Unit = {},
    onHomeFilterPressed: (HomeViewModel.HomeFilter) -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
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
            text = uiState.title,
            onTextChange = onTitleChanged,
            inlineLabel = false,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(32.dp))

        MoneyEntry(
            text = uiState.price,
            label = "Price",
            onPressed = onPricePressed,
            modifier = Modifier
                .defaultHorizontalPadding()
                .align(Alignment.Start)
        )

        Spacer(Modifier.height(32.dp))

        ResellTextEntry(
            label = "Item Description",
            text = uiState.description,
            onTextChange = onDescriptionChanged,
            inlineLabel = false,
            multiLineHeight = 255.dp,
            singleLine = false,
            // TODO: probably wrong max lines
            maxLines = 20,
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

            items(
                items = HomeViewModel.HomeFilter.entries.minus(HomeViewModel.HomeFilter.RECENT)
            ) { filter ->
                ResellTag(
                    text = filter.name.lowercase().replaceFirstChar {
                        it.uppercase()
                    },
                    active = uiState.activeFilters.contains(filter),
                    onClick = {
                        onHomeFilterPressed(filter)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.size(Padding.medium))
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}
