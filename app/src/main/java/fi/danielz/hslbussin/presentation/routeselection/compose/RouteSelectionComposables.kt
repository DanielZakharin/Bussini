package fi.danielz.hslbussin.presentation.routeselection.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import fi.danielz.hslbussin.presentation.routeselection.RouteSelectionViewModel
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData

@ExperimentalAnimationApi
@Composable
fun RouteSelectionScreen() {
    val viewModel: RouteSelectionViewModel = viewModel()
    val routesState = viewModel.dataSource.routes.collectAsState(initial = emptyList())
    val errorsState = viewModel.dataSource.errors.collectAsState(initial = null)
    HSLBussinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            errorBanner(errorState = errorsState)
            ScalingLazyColumn(
                verticalArrangement = Arrangement.Center,
            ) {
                items(routesState.value.size) {
                    routeRow(route = routesState.value[it])
                }
            }
        }
    }
}

@Composable
fun routeRow(route: RouteData) {
    Card(onClick = {
        // TODO
    }) {
        Row {
            Icon(imageVector = Icons.Default.DirectionsBus, contentDescription = "")
            Text(text = route.name, textAlign = TextAlign.Center)
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun errorBanner(errorState: State<List<Error>?>) {
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

@ExperimentalAnimationApi
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    RouteSelectionScreen()
}