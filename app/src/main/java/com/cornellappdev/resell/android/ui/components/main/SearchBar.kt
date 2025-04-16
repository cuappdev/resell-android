package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

@Composable
fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(43.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(color = Wash)
                .padding(start = 15.dp, top = 10.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search Icon",
                tint = Secondary
            )
            Text(text = "Search", style = Style.body2, color = Secondary)
        }
//        Icon(
////            painter = painterResource(R.drawable.ic)
//        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    Box(modifier = Modifier.background(color = Color.White)) {
        SearchBar(onClick = {})
    }
}