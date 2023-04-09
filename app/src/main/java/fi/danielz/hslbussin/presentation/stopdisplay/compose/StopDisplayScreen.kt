package fi.danielz.hslbussin.presentation.stopdisplay.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.airbnb.lottie.compose.*
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.compose.SelectionHeader
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme
import com.apollographql.apollo3.api.Error as ApolloError


sealed interface StopDisplayScreenUIState {
    val departures: List<StopSingleDepartureData>
    val errors: List<ApolloError>
    fun routeTitle(): String = ""

    data class Success(
        override val departures: List<StopSingleDepartureData>,
        private val routeTitle: String
    ) : StopDisplayScreenUIState {
        override val errors: List<ApolloError> = emptyList()
        override fun routeTitle(): String = routeTitle
    }

    data class Error(
        override val errors: List<com.apollographql.apollo3.api.Error>
    ) : StopDisplayScreenUIState {
        override val departures: List<StopSingleDepartureData> = emptyList()
    }

    class Loading : StopDisplayScreenUIState {
        override val departures: List<StopSingleDepartureData> = emptyList()
        override val errors: List<ApolloError> = emptyList()
    }
}

@Composable
fun StopDisplayScreen(
    uiState: StopDisplayScreenUIState,
    ticker: State<Long>,
    onSwitchRoutePressed: () -> Unit,
) {
    HSLBussinTheme {
        when (uiState) {
            is StopDisplayScreenUIState.Loading -> {
                CircularProgressIndicator()
            }
            is StopDisplayScreenUIState.Error -> {
                // TODO
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
                                        onClick = onSwitchRoutePressed,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = "Switch route"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uiState.routeTitle(),
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }
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
                        })
                }
            }
        }
    }
}
