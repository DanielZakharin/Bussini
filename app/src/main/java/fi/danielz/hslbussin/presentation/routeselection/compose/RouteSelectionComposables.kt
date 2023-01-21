package fi.danielz.hslbussin.presentation.routeselection.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

typealias RouteClick = (RouteData) -> Unit

@ExperimentalAnimationApi
@Composable
fun RouteSelectionScreen(
    routesState: State<List<RouteData>>,
    errorsState: State<List<Error>?>,
    onRouteSelectedClick: RouteClick,
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
                // add extra item for header
                items(routesState.value.size + 1) {
                    if (it == 0) {
                        RouteSelectionHeader(
                            routesState,
                            errorsState
                        )
                    } else {
                        val adjustedIndex = it - 1
                        RouteRow(
                            route = routesState.value[adjustedIndex],
                            onRouteSelectedClick = onRouteSelectedClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RouteSelectionHeader(routeState: State<List<RouteData>?>, errorState: State<List<Error>?>) {
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
        val text = if (loading) "Loading routes..." else "Select your bus route"
        Row {
            Text(text = text, textAlign = TextAlign.Center)
            if (loading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun RouteRow(onRouteSelectedClick: RouteClick, route: RouteData) {
    Card(onClick = {
        onRouteSelectedClick(route)
    }) {
        Row {
            Icon(imageVector = Icons.Default.DirectionsBus, contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = route.name,
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