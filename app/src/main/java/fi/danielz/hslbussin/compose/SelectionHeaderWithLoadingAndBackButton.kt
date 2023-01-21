package fi.danielz.hslbussin.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData

@Composable
fun <T>SelectionHeaderWithLoadingAndBackButton (
    itemsState: State<List<T>?>,
    errorState: State<List<Error>?>,
    text: String,
    loadingText: String,
    onBackPressed: () -> Unit
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
        val loading = itemsState.value.isNullOrEmpty() && errorState.value.isNullOrEmpty()
        val displayText = if (loading) loadingText else text
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onBackPressed() },
                modifier = Modifier
                    .padding(0.dp)
                    .height(30.dp)
                    .width(30.dp)
                    .background(Color.Transparent),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = displayText, textAlign = TextAlign.Start)
            if (loading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
            }
        }
    }
}