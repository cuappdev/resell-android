package com.cornellappdev.resell.android.ui.components.availability.helper
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.global.ResellCheckboxRow
import com.cornellappdev.resell.android.ui.components.global.ResellSwitchRow
import com.cornellappdev.resell.android.ui.theme.Style

// TODO: very hard coded right now, should integrate networking here + implement viewmodel
@Composable
fun AvailabilityFilters(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF7F3F9))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {

        HorizontalDivider()

        ResellSwitchRow(
            title = "Google Calendar Access",
            checked = true,
            enabled = true,
            onCheckedChange = {
                // TODO
            }
        )

        HorizontalDivider()

        ResellSwitchRow(
            title = "Availability Sharing",
            checked = true,
            enabled = true,
            onCheckedChange = {
                // TODO
            }
        )

        HorizontalDivider()

        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Sub-Calendars",
                style = Style.body1,
                fontWeight = FontWeight.SemiBold
            )

            // TODO: replace dummy here with actual sub-calendars from the user
            val subCalendars = listOf("Personal", "Leetcode", "Youtube", "Capra")
            // TODO: this is just hard coded, make it not hard coded
            val checkedStates = remember { mutableStateMapOf<String, Boolean>().apply {
                subCalendars.forEach { put(it, it != "Personal" && it != "Capra") }
            }}

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subCalendars.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pair.forEach { name ->
                            ResellCheckboxRow(
                                title = name,
                                checked = checkedStates[name] ?: false,
                                enabled = true,
                                onCheckedChange = { checkedStates[name] = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AvailabilityFiltersPreview() {
    AvailabilityFilters()
}