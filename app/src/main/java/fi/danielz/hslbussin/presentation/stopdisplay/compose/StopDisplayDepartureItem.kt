package fi.danielz.hslbussin.presentation.stopdisplay.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData


/**
 * @param a state that updates with the current system time continously. Used to update time remaining text
TODO style this element
 */
@Composable
fun StopDisplayDepartureItem(
    item: StopSingleDepartureData,
    ticker: State<Long>
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = item.displayText(ticker.value),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}