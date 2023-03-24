package fi.danielz.hslbussin.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import com.apollographql.apollo3.api.Error

// TODO split into header & loading indicator separately
@Composable
fun SelectionHeaderWithLoadingIndicator(
    loading: Boolean,
    text: String,
    loadingText: String
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.DarkGray,
                        Color.Gray
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // simple loading indicator
        val displayText = if (loading) loadingText else text
        Row {
            Text(text = displayText, textAlign = TextAlign.Center)
            if (loading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
            }
        }
    }
}