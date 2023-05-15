package fi.danielz.bussini.presentation.stopselection.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineStops
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.bussini.compose.ErrorWithRetryButton
import com.apollographql.apollo3.api.Error as ApolloError
import fi.danielz.bussini.compose.IconRow
import fi.danielz.bussini.compose.SelectionHeader
import fi.danielz.bussini.presentation.stopselection.model.StopData
import fi.danielz.bussini.presentation.theme.BussiniTheme

sealed interface StopSelectionScreenUIState {
    val stops: List<StopData>
    val errors: List<ApolloError>

    data class Success(
        override val stops: List<StopData>,
    ) : StopSelectionScreenUIState {
        override val errors: List<ApolloError> = emptyList()
    }

    data class Error(
        override val errors: List<ApolloError>
    ) : StopSelectionScreenUIState {
        override val stops: List<StopData> = emptyList()
    }

    class Loading : StopSelectionScreenUIState {
        override val stops: List<StopData> = emptyList()
        override val errors: List<ApolloError> = emptyList()
    }
}

interface StopSelectionClickHandler {
    fun onStopSelectedClick(stopGtfsId: String)
    fun onReloadClick()
}

@Composable
fun StopSelectionScreen(
    uiState: StopSelectionScreenUIState,
    clickHandler: StopSelectionClickHandler
) {
    BussiniTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            when (uiState) {
                is StopSelectionScreenUIState.Success -> {
                    ScalingLazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // add extra item for header
                        items(uiState.stops.size + 1) {
                            if (it == 0) {
                                SelectionHeader(
                                    "Select Stop"
                                )
                            } else {
                                val adjustedIndex = it - 1
                                val stop = uiState.stops[adjustedIndex]
                                IconRow(
                                    item = stop,
                                    onClick = { clickHandler.onStopSelectedClick(stop.gtfsId) },
                                    text = { it.name },
                                    imageVector = Icons.Default.AirlineStops
                                )
                            }
                        }
                    }
                }
                is StopSelectionScreenUIState.Error -> {
                    ErrorWithRetryButton(onRetryClick = clickHandler::onReloadClick)
                }
                is StopSelectionScreenUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

        }
    }
}