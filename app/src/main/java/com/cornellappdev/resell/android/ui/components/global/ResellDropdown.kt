package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellFilter
import com.cornellappdev.resell.android.ui.theme.Secondary

@Composable
fun Dropdown(onSelectSortBy: (ResellFilter.SortBy) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Icon(
            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
            contentDescription = "More options",
            tint = Secondary,
            modifier = Modifier.clickable { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(13.dp)
            )
        ) {
            for (sortBy in ResellFilter.SortBy.entries) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sortBy.label,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelectSortBy(sortBy)
                        expanded = false
                    }
                )
            }
        }
    }

}