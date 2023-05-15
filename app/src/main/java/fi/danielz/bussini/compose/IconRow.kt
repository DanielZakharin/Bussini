package fi.danielz.bussini.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Column(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            )
            .clickable {
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
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Preview
@Composable
fun PreviewIconRow() {
    IconRow(
        item = "Preview",
        text = { it },
        imageVector = Icons.Default.DirectionsBus,
        onClick = {})
}