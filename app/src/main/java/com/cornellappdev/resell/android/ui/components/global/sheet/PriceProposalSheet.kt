package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.ui.theme.rubikFamily
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun PriceProposalSheet(
    priceProposalSheetViewModel: PriceProposalSheetViewModel = hiltViewModel()
) {
    val uiState = priceProposalSheetViewModel.collectUiStateValue()

    PriceProposalSheetContent(
        uiState = uiState,
        onNumberPressed = priceProposalSheetViewModel::onNumberPressed,
        onDotPressed = priceProposalSheetViewModel::onDotPressed,
        onDeletePressed = priceProposalSheetViewModel::onDeletePressed,
        onConfirmPressed = priceProposalSheetViewModel::onConfirmPressed,
    )
}

@Composable
private fun PriceProposalSheetContent(
    uiState: PriceProposalSheetViewModel.PriceProposalStateUi,
    onNumberPressed: (String) -> Unit = {},
    onDotPressed: () -> Unit = {},
    onDeletePressed: () -> Unit = {},
    onConfirmPressed: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultHorizontalPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(15.dp))

        Text(
            text = uiState.title,
            style = Style.heading3,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(32.dp))

        TextEntry(
            price = uiState.price,
            indicator = uiState.canPressNumber,
        )

        Spacer(Modifier.height(16.dp))

        val hPadding = 40.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPadding)
        ) {
            EntryButton("1") {
                onNumberPressed("1")
            }

            EntryButton("2") {
                onNumberPressed("2")
            }

            EntryButton("3") {
                onNumberPressed("3")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPadding)
        ) {
            EntryButton("4") {
                onNumberPressed("4")
            }

            EntryButton("5") {
                onNumberPressed("5")
            }

            EntryButton("6") {
                onNumberPressed("6")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPadding)
        ) {
            EntryButton("7") {
                onNumberPressed("7")
            }

            EntryButton("8") {
                onNumberPressed("8")
            }

            EntryButton("9") {
                onNumberPressed("9")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPadding)
        ) {
            EntryButton(".") {
                onDotPressed()
            }

            EntryButton("0") {
                onNumberPressed("0")
            }

            EntryButton("<") {
                onDeletePressed()
            }
        }

        Spacer(Modifier.height(32.dp))

        ResellTextButton(
            text = uiState.confirmText,
            onClick = onConfirmPressed
        )

        Spacer(Modifier.height(46.dp))
    }
}

@Composable
private fun RowScope.EntryButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier
            .weight(1f),
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = rubikFamily,
                fontWeight = FontWeight(500),
                color = Color.Black,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun TextEntry(
    price: String,
    indicator: Boolean,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Indicator oscillate")
    val oscillatingFloat by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Indicator oscillate"
    )

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$",
            style = TextStyle(
                fontSize = 48.sp,
                fontFamily = rubikFamily,
                fontWeight = FontWeight(400),
                color = AppDev,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(Modifier.width(12.dp))

        Row(
            modifier = Modifier
                .height(81.dp)
                .widthIn(min = 137.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Wash),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(17.dp))

            Text(
                text = price,
                style = TextStyle(
                    fontSize = 36.sp,
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight(400),
                    color = Color.Black,
                    letterSpacing = 5.04.sp,
                )
            )

            Spacer(Modifier.width(5.dp))

            Box(
                modifier = Modifier
                    .background(
                        ResellPurple.copy(
                            alpha = if (indicator) {
                                oscillatingFloat
                            } else {
                                0f
                            }
                        )
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .height(46.dp)
                    .width(3.dp)
            )

            Spacer(Modifier.width(17.dp))
        }
    }
}

@Preview
@Composable
private fun TextEntryPreview() = ResellPreview {
    TextEntry(price = "123", indicator = true)
}

@Preview
@Composable
private fun SheetContentPreview() = ResellPreview {
    PriceProposalSheetContent(
        uiState = PriceProposalSheetViewModel.PriceProposalStateUi(
            title = "Title",
            price = "123",
        )
    )
}
