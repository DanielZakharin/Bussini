package fi.danielz.bussini.presentation.stopdisplay.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.bussini.compose.ErrorWithRetryButton
import fi.danielz.bussini.compose.SelectionHeader
import fi.danielz.bussini.presentation.stopdisplay.model.StopSingleDepartureData
import fi.danielz.bussini.presentation.theme.BussiniTheme
import kotlinx.coroutines.flow.emptyFlow


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
    BussiniTheme {
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
                                Column(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,

                                    ) {
                                    Button(
                                        onClick = clickHandler::onSwitchRoutePressed,
                                        modifier = Modifier.fillMaxWidth(0.75F)
                                    ) {
                                        Text(
                                            text = "Switch Line",
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Line: ${uiState.routeTitle()}",
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // case: no departures
                            if (uiState.departures.isEmpty()) {
                                item {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        SelectionHeader(text = "No departures found. Switch route, or refresh")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(onClick = clickHandler::onRetryPressed) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Retry"
                                            )
                                        }
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

@Composable
@Preview
private fun PreviewStopDisplayScren() {
    val stopDepData = object : StopSingleDepartureData {
        override val timeOfDeparture: Long
            get() = 1L

        override fun timeUntilDeparture(fromTimePoint: Long): Long {
            return 1L
        }

        override fun displayText(fromTimePoint: Long): String {
            return "15min"
        }

    }
    val ticker = emptyFlow<Long>().collectAsState(initial = 0L)
    val click = object : StopDisplayClickHandler {
        override fun onSwitchRoutePressed() {

        }

        override fun onRetryPressed() {
        }

    }
    StopDisplayScreen(
        uiState = StopDisplayScreenUIState.Success(
            departures = listOf(stopDepData),
            "55"
        ),
        ticker = ticker,
        clickHandler = click
    )
}
