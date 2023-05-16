package fi.danielz.bussini.presentation.stopdisplay.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.danielz.bussini.presentation.stopdisplay.model.StopSingleDepartureData


/**
 * @param ticker state that updates with the current system time continously. Used to update time remaining text
TODO style this element
 */
@Composable
fun StopDisplayDepartureItem(
    item: StopSingleDepartureData,
    ticker: State<Long>
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = item.displayText(ticker.value),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(thickness = 0.5.dp)
    }
}