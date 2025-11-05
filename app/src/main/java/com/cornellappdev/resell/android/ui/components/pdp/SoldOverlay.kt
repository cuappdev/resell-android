import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cornellappdev.resell.android.ui.theme.Style

// TODO implemented but should test + integrate into pdp composable
@Composable
fun SoldOverlay(
    modifier: Modifier = Modifier,
    text: String = "Item Sold",
    backgroundColor: Color = Color.White.copy(alpha = 0.4f),
    textColor: Color = Color.Black,
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = Style.title3
        )
    }
}
