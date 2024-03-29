package fi.danielz.bussini.presentation.routeselection.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.bussini.R
import fi.danielz.bussini.compose.ErrorWithRetryButton
import fi.danielz.bussini.compose.IconRow
import fi.danielz.bussini.compose.SelectionHeader
import fi.danielz.bussini.presentation.routeselection.model.RouteData
import fi.danielz.bussini.presentation.theme.BussiniTheme

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

interface RouteSelectionClickHandler {
    fun onRouteSelected(data: RouteData)
    fun onRetryErrorClick()
}

@Composable
fun RouteSelectionScreen(
    uiState: RouteSelectionScreenUIState,
    clickHandler: RouteSelectionClickHandler
) {
    BussiniTheme {
        when (uiState) {
            is RouteSelectionScreenUIState.Error -> {
                Box(modifier = Modifier.padding(8.dp)) {
                    ErrorWithRetryButton(onRetryClick = clickHandler::onRetryErrorClick)
                }
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
                            SelectionHeader(stringResource(R.string.route_selection_select_route))
                        }
                        // row + divider
                        items(uiState.routes.size) { index ->
                            IconRow(
                                item = uiState.routes[index],
                                onClick = clickHandler::onRouteSelected,
                                imageVector = Icons.Default.DirectionsBus,
                                text = { it.fullName }
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
        RouteSelectionScreenUIState.Error(emptyList()),
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
    RouteSelectionScreen(uiState = uiState, clickHandler = object : RouteSelectionClickHandler {
        override fun onRouteSelected(data: RouteData) {

        }

        override fun onRetryErrorClick() {

        }
    })
}