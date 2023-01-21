package fi.danielz.hslbussin.presentation.stopselection.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineStops
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.compose.ErrorBanner
import fi.danielz.hslbussin.compose.IconRow
import fi.danielz.hslbussin.compose.SelectionHeaderWithLoadingAndBackButton
import fi.danielz.hslbussin.presentation.stopselection.model.StopData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StopSelectionScreen(stopsState: State<List<StopData>>, errorState: State<List<Error>?>, onBackPressed: () -> Unit) {
    HSLBussinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            ErrorBanner(errorState = errorState)
            ScalingLazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // add extra item for header
                items(stopsState.value.size + 1) {
                    if (it == 0) {
                        SelectionHeaderWithLoadingAndBackButton(
                            stopsState,
                            errorState,
                            "Select stop",
                            "Loading stops...",
                            onBackPressed
                        )
                    } else {
                        val adjustedIndex = it - 1
                        IconRow(
                            item = stopsState.value[adjustedIndex],
                            onClick = {},
                            text = { it.name },
                            imageVector = Icons.Default.AirlineStops
                        )
                    }
                }
            }
        }
    }
}