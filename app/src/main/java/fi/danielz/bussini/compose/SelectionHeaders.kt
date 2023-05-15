package fi.danielz.bussini.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text


@Composable
fun SelectionHeader(
    text: String
) {
    Text(text = text, textAlign = TextAlign.Start)
}

@Preview
@Composable
fun PreviewSelectionHeader() {
    SelectionHeader("This is header")
}