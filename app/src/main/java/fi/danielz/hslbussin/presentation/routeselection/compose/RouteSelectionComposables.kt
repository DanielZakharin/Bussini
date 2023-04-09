@file:OptIn(ExperimentalAnimationApi::class)

package fi.danielz.hslbussin.presentation.routeselection.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.hslbussin.compose.ErrorBanner
import fi.danielz.hslbussin.compose.IconRow
import fi.danielz.hslbussin.compose.SelectionHeader
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

sealed interface RouteSelectionScreenUIState {
    val routes: List<RouteData>
    val errors: List<com.apollographql.apollo3.api.Error>

    data class Error(
        override val errors: List<com.apollographql.apollo3.api.Error>
    ) : RouteSelectionScreenUIState {
        override val routes: List<RouteData> = emptyList()
    }

    data class Success(
        override val routes: List<RouteData>
    ) : RouteSelectionScreenUIState {
        override val errors: List<com.apollographql.apollo3.api.Error> = emptyList()
    }

    class Loading : RouteSelectionScreenUIState {
        override val routes: List<RouteData> = emptyList()
        override val errors: List<com.apollographql.apollo3.api.Error> = emptyList()
    }
}

@Composable
fun RouteSelectionScreen(
    uiState: RouteSelectionScreenUIState,
    onRouteSelectedClick: (RouteData) -> Unit = {}
) {
    HSLBussinTheme {
        when (uiState) {
            is RouteSelectionScreenUIState.Error -> {
                // TODO show only error
            }
            is RouteSelectionScreenUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    ScalingLazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // add extra item for header
                        item {
                            SelectionHeader("Select bus route")
                        }
                        // row + divider
                        items(uiState.routes.size) { index ->
                            IconRow(
                                item = uiState.routes[index],
                                onClick = onRouteSelectedClick,
                                imageVector = Icons.Default.DirectionsBus,
                                text = { it.name }
                            )
                        }
                    }
                }
            }
        }
    }
}

// preview

private class StateProvider : PreviewParameterProvider<RouteSelectionScreenUIState> {
    override val count: Int = 2
    override val values: Sequence<RouteSelectionScreenUIState> = sequenceOf(
        RouteSelectionScreenUIState.Loading(),
        RouteSelectionScreenUIState.Success(
            emptyList() // TODO
        ),
    )
}

@Preview
@Composable
private fun RouteSelectionScreenPreview(
    @PreviewParameter(StateProvider::class) uiState: RouteSelectionScreenUIState
) {
    RouteSelectionScreen(uiState = uiState)
}