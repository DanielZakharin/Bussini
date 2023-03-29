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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData


@Composable
fun SelectionHeader(
    text: String
) {
    Text(text = text, textAlign = TextAlign.Start)
}


@Composable
fun SelectionHeaderWithBackButton(
    text: String,
    onBackPressed: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
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
            SelectionHeader(text = text)
        }
    }
}

@Preview
@Composable
fun PreviewSelectionHeaderWithBackButton() {
    SelectionHeaderWithBackButton("This is header", {})
}