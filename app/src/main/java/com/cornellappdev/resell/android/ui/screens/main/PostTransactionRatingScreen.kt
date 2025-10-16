package com.cornellappdev.resell.android.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Text
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellRatingBar
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style.body1
import com.cornellappdev.resell.android.ui.theme.Style.body2
import com.cornellappdev.resell.android.ui.theme.Style.heading3
import com.cornellappdev.resell.android.ui.theme.Style.title1

import com.cornellappdev.resell.android.viewmodel.main.PostTransactionRatingViewModel
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
            "Item Name",
            painterResource(R.drawable.ic_appdev),
            "00.00",
            "Seller Name",
            "Month, 00, 0000"
        )
        HorizontalDivider()

        Text(
            text = "Transaction Review",
            style = heading3,
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
            placeholder = "How was your transaction experience with SELLER NAME? (Optional)",
            textFontStyle = body2,
            multiLineHeight = 117.dp,
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
        )

        val annotatedText = buildAnnotatedString {
            append("Had issues? Submit ")

            // Annotating the clickable part
            pushStringAnnotation(
                tag = "feedback_link",
                annotation = "nav_to_feedback"
            )

            withStyle(
                style = SpanStyle(
                    color = ResellPurple,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("feedback")
            }
             pop() // End of the clickable part
        }

        Text(
            text = annotatedText,
            style = body2,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    annotatedText.getStringAnnotations(tag = "feedback_link", start = 0, end = annotatedText.length)
                        .firstOrNull()?.let {
                             postTransactionRatingViewModel.onFeedbackClicked()
                        }
                }
        )
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
//TODO - modify to pass in imageURL instead of painter when connecting to backend/viewmodel
private fun ItemInfo(
    itemName: String,
    image: Painter,
    price: String,
    sellerName: String,
    date: String
) {
    Column {
        Text(text = "Purchase Summary", style = heading3, modifier = Modifier.padding(top = 36.dp))

        Row(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
            //TODO - plagiarize ResellCard AsyncImage when UI is done/backend connected
            Image(
                painter = painterResource(R.drawable.ic_appdev),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = itemName, style = title1)
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "$$price", style = body1)
                }
                Text(
                    "Sold by $sellerName",
                    style = body2,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Text("Purchased on $date", style = body2)

            }
        }
    }
}



@Preview
@Composable
private fun RatingScreenPreview() {
    PostTransactionRatingScreen()
}