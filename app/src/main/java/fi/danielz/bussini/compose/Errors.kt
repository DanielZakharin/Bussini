package fi.danielz.bussini.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.danielz.bussini.R

@Composable
fun ErrorWithRetryButton(errorText: String = "An error has occured! Please try again in a moment.", onRetryClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_bus_error),
            contentDescription = "Error Icon",
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(color = Color.White),
            modifier = Modifier.height(60.dp).width(60.dp)
        )
        Text(
            text = errorText,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetryClick) {
            Icon(Icons.Default.Refresh, contentDescription = "Retry")
        }
    }
}

@Preview
@Composable
fun PreviewErrorWithRetryButton() = ErrorWithRetryButton(errorText = "Preview Error") {}