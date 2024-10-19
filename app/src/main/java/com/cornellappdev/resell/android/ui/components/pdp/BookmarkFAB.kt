package com.cornellappdev.resell.android.ui.components.pdp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Wash

@Composable
fun BookmarkFAB(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // If 1f, then button is selected. If 0f, then button is not selected
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        label = "button progress"
    )

    Surface(
        modifier = modifier.size(72.dp),
        shape = CircleShape,
        color = Wash.copy(alpha = progress * .1f + 0.9f),
        onClick = onClick
    ) {
        Box(modifier = Modifier.size(72.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_save_fill),
                contentDescription = "save",
                modifier = Modifier
                    .size(width = 21.dp, height = 27.dp)
                    .alpha(progress)
                    .align(Alignment.Center),
                tint = ResellPurple
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_save_nofill),
                contentDescription = "save",
                modifier = Modifier
                    .size(width = 21.dp, height = 27.dp)
                    .alpha(1f - progress)
                    .align(Alignment.Center),
                tint = ResellPurple
            )
        }
    }
}

@Preview
@Composable
private fun BookmarkFABPreview() = ResellPreview {
    var selected by remember { mutableStateOf(false) }
    BookmarkFAB(selected = selected, onClick = { selected = !selected })
}
