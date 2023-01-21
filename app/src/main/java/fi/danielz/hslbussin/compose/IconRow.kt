package fi.danielz.hslbussin.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text

/**
 * Simple row for lists with an icon and single row of text
 */
@Composable
fun <T> IconRow(
    item: T,
    text: (T) -> String,
    imageVector: ImageVector,
    onClick: (T) -> Unit
) {
    Card(onClick = {
        onClick(item)
    }) {
        Row {
            Icon(imageVector = imageVector, contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text(item),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}