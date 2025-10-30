package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellRatingBar
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.viewmodel.main.PostTransactionRatingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostTransactionRatingScreen(
    postTransactionRatingViewModel: PostTransactionRatingViewModel = hiltViewModel()
) {

    val postTransactionUiState = postTransactionRatingViewModel.collectUiStateValue()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
    ) {
        ResellHeader(
            title = "Completed Transaction",
            leftPainter = R.drawable.ic_chevron_left,
            onLeftClick = {
                postTransactionRatingViewModel.onBackArrow()
            }
        )
        ItemInfo(
            itemName = postTransactionUiState.itemName,
            imageUrl = postTransactionUiState.imageUrl,
            price = postTransactionUiState.price,
            sellerName = postTransactionUiState.sellerName,
            date = postTransactionUiState.date
        )
        HorizontalDivider()

        Text(
            text = "Transaction Review",
            style = Style.heading3,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )
        ResellRatingBar(
            rating = postTransactionUiState.rating,
            onRatingChanged = {
                postTransactionRatingViewModel.onRatingChanged(it)
            }
        )

        ResellTextEntry(
            text = postTransactionUiState.reviewText,
            onTextChange = {
                postTransactionRatingViewModel.onReviewTextChanged(it)
            },
            inlineLabel = false,
            singleLine = false,
            placeholder = "How was your transaction experience with ${postTransactionUiState.sellerName}? (Optional)",
            textFontStyle = Style.body2,
            multiLineHeight = 117.dp,
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Had issues? Submit ",
                style = Style.body2
            )
            Text(
                text = "feedback",
                style = Style.body2.copy(
                    color = ResellPurple,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    postTransactionRatingViewModel.onFeedbackClicked()
                }
            )
        }
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
            ResellTextButton(
                text = "Submit Review",
                onClick = {
                    postTransactionRatingViewModel.submitReview()
                },
                modifier = Modifier.padding(bottom = 44.dp)
            )
        }

    }
}

@Composable
private fun ItemInfo(
    itemName: String,
    imageUrl: String,
    price: String,
    sellerName: String,
    date: Date?
) {
    val formattedDate = remember(date) {
        date.let { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Month 00, 0000"
    }

    Column {
        Text(
            text = "Purchase Summary",
            style = Style.heading3,
            modifier = Modifier.padding(top = 36.dp)
        )

        Row(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
            //TODO - plagiarize ResellCard AsyncImage when UI is done/backend connected
            Image(
                painter = painterResource(R.drawable.ic_appdev),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = itemName, style = Style.title1)
                    // TODO : make this look better
                    Text(text = "•", color = Color.Black)
                    Text(text = "$$price", style = Style.body1)
                }
                Text(
                    "Sold by $sellerName",
                    style = Style.body2,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Text("Purchased on $formattedDate", style = Style.body2)

            }
        }
    }
}


@Preview
@Composable
private fun RatingScreenPreview() {
    PostTransactionRatingScreen()
}
