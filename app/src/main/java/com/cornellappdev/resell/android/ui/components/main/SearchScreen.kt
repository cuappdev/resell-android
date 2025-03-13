package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ResellListingsScroll
import com.cornellappdev.resell.android.ui.components.global.ResellLoadingListingScroll
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun SearchScreen(
    query: String,
    onQueryChanged: (String) -> Unit,
    onExit: () -> Unit,
    onListingPressed: (Listing) -> Unit,
    listings: ResellApiResponse<List<Listing>>,
    placeholder: String,
    focusKeyboard: UIEvent<Unit>?
) {
    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusKeyboard) {
        focusKeyboard?.consume {
            focusRequester.requestFocus()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchHeader(
            query = query,
            onQueryChanged = onQueryChanged,
            onExit = onExit,
            placeholder = placeholder,
            focusRequester = focusRequester
        )

        when (listings) {
            is ResellApiResponse.Success -> {
                ResellListingsScroll(
                    listings = listings.data,
                    onListingPressed = onListingPressed,
                    paddedTop = 12.dp
                )
            }

            is ResellApiResponse.Error -> {

            }

            is ResellApiResponse.Pending -> {
                ResellLoadingListingScroll(
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchHeader(
    query: String = "",
    onQueryChanged: (String) -> Unit = {},
    onExit: () -> Unit = {},
    placeholder: String = "Search...",
    focusRequester: FocusRequester? = null,
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(top = 16.dp)
            .defaultHorizontalPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ResellTextEntry(
            text = query,
            onTextChange = onQueryChanged,
            placeholder = placeholder,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester ?: FocusRequester()),
        )

        Icon(
            painter = painterResource(R.drawable.ic_exit),
            contentDescription = "exit",
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 16.dp)
                .clickableNoIndication {
                    onExit()
                }
        )
    }
}
