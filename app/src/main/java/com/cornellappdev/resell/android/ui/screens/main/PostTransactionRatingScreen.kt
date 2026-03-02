package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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

    PostTransactionRatingScreenContent(
        uiState = postTransactionUiState,
        onBackArrow = postTransactionRatingViewModel::onBackArrow,
        onRatingChanged = postTransactionRatingViewModel::onRatingChanged,
        onReviewTextChanged = postTransactionRatingViewModel::onReviewTextChanged,
        onFeedbackClicked = postTransactionRatingViewModel::onFeedbackClicked,
        onSubmitReview = postTransactionRatingViewModel::submitReview
    )
}

@Composable
private fun PostTransactionRatingScreenContent(
    uiState: PostTransactionRatingViewModel.PostTransactionRatingUiState,
    onBackArrow: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onReviewTextChanged: (String) -> Unit,
    onFeedbackClicked: () -> Unit,
    onSubmitReview: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            ResellHeader(
                title = "Completed Transaction",
                leftPainter = R.drawable.ic_chevron_left,
                onLeftClick = onBackArrow
            )
            ItemInfo(
                itemName = uiState.itemName,
                imageUrl = uiState.imageUrl,
                price = uiState.price,
                sellerName = uiState.sellerName,
                date = uiState.date
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Transaction Review",
                style = Style.heading3
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResellRatingBar(
                rating = uiState.rating,
                onRatingChanged = onRatingChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResellTextEntry(
                text = uiState.reviewText,
                onTextChange = onReviewTextChanged,
                inlineLabel = false,
                singleLine = false,
                placeholder = "How was your transaction experience with ${uiState.sellerName}? (Optional)",
                textFontStyle = Style.body2,
                multiLineHeight = 117.dp
            )

            Spacer(modifier = Modifier.height(36.dp))

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
                        onFeedbackClicked()
                    }
                )
            }
        }

        ResellTextButton(
            text = "Submit Review",
            onClick = onSubmitReview,
            modifier = Modifier.padding(bottom = 44.dp)
        )
    }
}

@Composable
private fun ItemInfo(
    itemName: String,
    imageUrl: String,
    price: String,
    sellerName: String,
    date: Date
) {
    val formattedDate = remember(date) {
        date.let { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Month 00, 0000"
    }

    Column {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "Purchase Summary",
            style = Style.heading3,
        )
        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Item image",
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = itemName, style = Style.title1)
                    Text(text = "•", color = Color.Black)
                    Text(text = "$$price", style = Style.body1)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Sold by $sellerName",
                    style = Style.body2,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Purchased on $formattedDate", style = Style.body2)

            }
        }
    }
}


@Preview
@Composable
private fun RatingScreenPreview() {
    PostTransactionRatingScreenContent(
        uiState = PostTransactionRatingViewModel.PostTransactionRatingUiState(
            itemName = "Item Name",
            imageUrl = "",
            price = "10.00",
            sellerName = "Seller Name",
            date = Date(),
            rating = 0,
            reviewText = ""
        ),
        onBackArrow = {},
        onRatingChanged = {},
        onReviewTextChanged = {},
        onFeedbackClicked = {},
        onSubmitReview = {}
    )
}
