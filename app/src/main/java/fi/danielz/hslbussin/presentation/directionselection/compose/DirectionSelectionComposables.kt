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
    onBackPressed: () -> Unit
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
                        DirectionsSelectionHeader(routesState, errorsState, onBackPressed)
                    } else {
                        val adjustedIndex = it - 1
                        routesState.value[it].directions?.get(adjustedIndex)?.let { direction ->
                            DirectionRow(selectedRouteId, direction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectionsSelectionHeader(
    routeState: State<List<RouteData>?>,
    errorState: State<List<Error>?>,
    onBackPressed: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.DarkGray,
                        Color.Gray
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // simple loading indicator
        val loading = routeState.value.isNullOrEmpty() && errorState.value.isNullOrEmpty()
        val text = if (loading) "Loading directions..." else "Select direction"
        Row (modifier = Modifier.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { onBackPressed() },
                modifier = Modifier
                    .padding(0.dp)
                    .height(30.dp)
                    .width(30.dp)
                    .background(Color.Transparent),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, textAlign = TextAlign.Start)
            if (loading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DirectionRow(selectedRouteId: String, directionData: DirectionData) {
    Card(onClick = {
        //vm.onDirectionSelectedClick(selectedRouteId, directionData.directionId)
    }) {
        Row {
            Icon(imageVector = Icons.Default.CompareArrows, contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = directionData.name ?: "",
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ErrorBanner(errorState: State<List<Error>?>) {
    AnimatedVisibility(visible = !errorState.value.isNullOrEmpty()) {

        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = "An error occurred, please try again later")
            Button(content = {
                Text(text = "Retry")
            }, onClick = {
                // TODO
            })
        }
    }
}