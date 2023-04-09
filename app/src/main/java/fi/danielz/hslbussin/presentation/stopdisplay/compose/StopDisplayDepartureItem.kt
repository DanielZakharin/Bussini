package fi.danielz.hslbussin.presentation.stopdisplay.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData


// TODO style this element
@Composable
fun StopDisplayDepartureItem(
    item: StopSingleDepartureData
) {
    Row(modifier = Modifier
        //.fillMaxWidth()
        .padding(8.dp)) {
        Text(text = item.displayText, color = Color.White, textAlign = TextAlign.Center)
    }
}