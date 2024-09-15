package com.cornellappdev.android.resell.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.resell.R
import com.cornellappdev.android.resell.ui.theme.AppDev
import com.cornellappdev.android.resell.ui.theme.Style

@Composable
fun LandingScreen() {

}

@Preview
@Composable
private fun LandingContent(
    showButton: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_resell),
                contentDescription = null,
                modifier = Modifier.size(width = 96.dp, height = 130.dp)
            )

            Text(
                text = "resell",
                fontSize = 48.sp,
                style = Style.resellLogo,
            )
        }
        Spacer(modifier = Modifier.weight(3f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_appdev),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 6.5.dp)
                    .size(24.dp),
                tint = AppDev,
            )

            Text(
                text = "CornellAppDev",
                style = Style.appDev,
            )
        }

        Spacer(modifier = Modifier.weight(.5f))
    }
}
