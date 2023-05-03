package fi.danielz.hslbussin.presentation.stopdisplay.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.hslbussin.compose.ErrorWithRetryButton
import fi.danielz.hslbussin.compose.SelectionHeader
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme
import com.apollographql.apollo3.api.Error as ApolloError


sealed class StopDisplayScreenUIState {
    abstract val departures: List<StopSingleDepartureData>
    open val error: Exception? = null
    open fun routeTitle(): String = ""

    data class Success(
        override val departures: List<StopSingleDepartureData>,
        private val routeTitle: String
    ) : StopDisplayScreenUIState() {
        override fun routeTitle(): String = routeTitle
    }

    data class Error(
        override val error: Exception
    ) : StopDisplayScreenUIState() {
        override val departures: List<StopSingleDepartureData> = emptyList()
    }

    class Loading : StopDisplayScreenUIState() {
        override val departures: List<StopSingleDepartureData> = emptyList()
    }
}

interface StopDisplayClickHandler {
    fun onSwitchRoutePressed()
    fun onRetryPressed()
}

@Composable
fun StopDisplayScreen(
    uiState: StopDisplayScreenUIState,
    ticker: State<Long>,
    clickHandler: StopDisplayClickHandler
) {
    HSLBussinTheme {
        when (uiState) {
            is StopDisplayScreenUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.wear.compose.material.CircularProgressIndicator()
                }
            }
            is StopDisplayScreenUIState.Error -> {
                ErrorWithRetryButton(
                    onRetryClick = clickHandler::onRetryPressed
                )
            }
            is StopDisplayScreenUIState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    ScalingLazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        autoCentering = AutoCenteringParams(itemIndex = 2),
                        content = {
                            // Bus route number and switch button
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,

                                    ) {
                                    Button(
                                        onClick = clickHandler::onSwitchRoutePressed,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = "Switch route"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Line:\n${uiState.routeTitle()}",
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // case: no departures
                            if (uiState.departures.isEmpty() || true /* FIXME DEBUG */) {
                                item {
                                    SelectionHeader(text = "No departures found. Switch route, or refresh")
                                    Button(onClick = clickHandler::onRetryPressed) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                                    }
                                }
                            } else {
                                // first departure and header
                                item {
                                    SelectionHeader(text = "Next departure in:")
                                }
                                item {
                                    uiState.departures.firstOrNull()?.let { departure ->
                                        StopDisplayDepartureItem(item = departure, ticker)
                                    }
                                }
                                // subsequent departures
                                if (uiState.departures.size > 1) {
                                    item {
                                        SelectionHeader(text = "And then in:")
                                    }
                                }
                                items(uiState.departures.size) { index ->
                                    // skip first item, added previously
                                    if (index != 0) {
                                        val departure = uiState.departures[index]
                                        StopDisplayDepartureItem(item = departure, ticker)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}
