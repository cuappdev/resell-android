package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.util.clickableNoIndication

/**
 * The standard back chevron. Defaults to 24dp; a size in [modifier] takes precedence.
 */
@Composable
fun ResellBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Primary,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron_left),
        contentDescription = stringResource(R.string.cd_back),
        tint = tint,
        modifier = modifier
            .size(24.dp)
            .clickableNoIndication(onClick = onClick),
    )
}

@Preview(showBackground = true)
@Composable
private fun ResellBackButtonPreview() = ResellPreview {
    ResellBackButton(onClick = {})
}
