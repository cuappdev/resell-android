package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.RequestListing
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.util.richieUserInfo

@Composable
fun ResellRequestsScroll(
    requests: List<RequestListing>,
    onClick: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
    emptyState: @Composable () -> Unit,
) {
    if (requests.isEmpty()) {
        emptyState()
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        itemsIndexed(requests) { i, request ->
            RequestCard(
                title = request.title,
                description = request.description,
                matchCount = request.matches.size,
                onClick = {
                    onClick(i)
                },
                onDelete = {
                    onDelete(i)
                },
            )
        }
    }
}

@Preview
@Composable
private fun ResellRequestsScrollPreview()  = ResellPreview {
    ResellRequestsScroll(
        requests = listOf(
            RequestListing(
                title = "Title",
                description = "Description",
                matches = listOf(),
                id = "",
                user = richieUserInfo
            )
        ),
        onClick = {},
        onDelete = {},
        emptyState = {},
    )
}
