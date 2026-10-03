package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding


@Composable
fun ResellSwitchRow(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .defaultHorizontalPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Style.body1,
            fontWeight = FontWeight.SemiBold
        )

        Switch(
            checked = checked && enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = IconInactive,
                checkedTrackColor = ResellPurple,
                uncheckedTrackColor = Color.White,
                checkedBorderColor = ResellPurple,
                uncheckedBorderColor = IconInactive
            ),
            enabled = enabled,
        )
    }
}

@Preview
@Composable
private fun ResellSwitchRowPreview() {
    ResellSwitchRow(
        title = "Turn on notifications",
        checked = true,
        enabled = true,
        onCheckedChange = {}
    )
}