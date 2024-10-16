package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun WelcomeSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.defaultHorizontalPadding().fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Resell!",
            style = Style.heading3,
            color = ResellPurple
        )

        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_resell),
            contentDescription = null,
            modifier = Modifier.size(width = 53.dp, height = 73.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Thrifting and selling has never been this easy",
            style = Style.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 36.dp)
        )

        Spacer(modifier = Modifier.height(33.dp))

        ResellTextButton(
            text = "Get Started",
            onClick = onDismiss,
            containerType = ResellTextButtonContainer.PRIMARY
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}
