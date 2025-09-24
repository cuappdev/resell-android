package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

@Composable
fun ResellSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(43.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(color = Wash)
            .padding(start = 15.dp, top = 10.dp, bottom = 9.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),

        ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Secondary,
            modifier = Modifier.size(24.dp)
        )
        Text(text = "Search", style = Style.body2, color = Secondary)
    }
}

@Preview(showBackground = true)
@Composable
private fun ResellSearchBarPreview() {
    ResellSearchBar(onClick = {})
}