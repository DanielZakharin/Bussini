package fi.danielz.hslbussin.presentation.directionselection.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import fi.danielz.hslbussin.compose.*
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreenUIState
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

interface DirectionSelectionClickHandler {
    fun onDirectionSelected(routeId: String, directionId: Int)
    fun onBackPressed()
    fun onReloadClick()
}

/**
 * Similar to route selection
 */
@ExperimentalAnimationApi
@Composable
fun DirectionSelectionScreen(
    selectedRouteId: String,
    uiState: RouteSelectionScreenUIState,
    clickHandler: DirectionSelectionClickHandler
) {
    HSLBussinTheme {
        when (uiState) {
            is RouteSelectionScreenUIState.Error -> {
                ErrorWithRetryButton(onRetryClick = clickHandler::onReloadClick)
            }
            is RouteSelectionScreenUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RouteSelectionScreenUIState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    ScalingLazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val selectedRouteDirections = uiState.routes.find {
                            it.gtfsId == selectedRouteId
                        }?.directions ?: emptyList()
                        // add extra item for header
                        item {
                            SelectionHeader("Select direction")
                        }
                        items(selectedRouteDirections.size) { index ->
                            IconRow(
                                item = selectedRouteDirections[index],
                                text = { it.name ?: "" },
                                imageVector = Icons.Default.CompareArrows
                            ) {
                                it.directionId?.let { dir ->
                                    clickHandler.onDirectionSelected(selectedRouteId, dir)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
