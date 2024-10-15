package com.cornellappdev.resell.android.ui.components.settings

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun ResellWebView(
    modifier: Modifier = Modifier,
    url: String,
) {
    var mUrl by remember { mutableStateOf(url) }
    val context = LocalContext.current

    // Declare a string that contains a url
    LaunchedEffect(url) {
        mUrl = url
    }

    Box(modifier = modifier.fillMaxHeight(.8f)) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    this.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.webViewClient = CustomWebViewClient()
                }
            },
            update = {
                it.loadUrl(mUrl)
            },
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )

        Button(
            onClick = {
                // Open in browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppDev)
        ) {
            Text(
                text = "Open in Browser",
                color = Color.White,
                style = Style.body1
            )
        }
    }
}

class CustomWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null && url.startsWith("https://google.com")) {
            return true
        }
        return false
    }
}
