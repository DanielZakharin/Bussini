package fi.danielz.hslbussin.presentation.directionselection.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineStops
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.compose.ErrorBanner
import fi.danielz.hslbussin.compose.IconRow
import fi.danielz.hslbussin.compose.SelectionHeaderWithLoadingAndBackButton
import fi.danielz.hslbussin.compose.SelectionHeaderWithLoadingIndicator
import fi.danielz.hslbussin.presentation.directionselection.model.DirectionData
import fi.danielz.hslbussin.presentation.routeselection.RouteSelectionViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

/**
 * Similar to route selection
 */
@ExperimentalAnimationApi
@Composable
fun DirectionSelectionScreen(
    selectedRouteId: String,
    errorsState: State<List<Error>?>,
    routesState: State<List<RouteData>>,
    onBackPressed: () -> Unit,
    onItemSelected: (routeId: String, directionId: Int) -> Unit
) {
    HSLBussinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            ErrorBanner(errorState = errorsState)
            ScalingLazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val selectedRouteDirections = routesState.value.find {
                    it.gtfsId == selectedRouteId
                }?.directions ?: emptyList()
                // add extra item for header
                items(selectedRouteDirections.size + 1) {
                    if (it == 0) {
                        SelectionHeaderWithLoadingAndBackButton(
                            routesState,
                            errorsState,
                            "Select direction",
                            "Loading...",
                            onBackPressed
                        )
                    } else {
                        val adjustedIndex = it - 1
                        IconRow(
                            item = selectedRouteDirections[adjustedIndex],
                            text = { it.name ?: "" },
                            imageVector = Icons.Default.CompareArrows
                        ) {
                            it.directionId?.let { dir ->
                                onItemSelected(selectedRouteId, dir)
                            }
                        }
                    }
                }
            }
        }
    }
}
