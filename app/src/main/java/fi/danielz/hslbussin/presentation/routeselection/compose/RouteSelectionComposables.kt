package fi.danielz.hslbussin.presentation.routeselection.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import fi.danielz.hslbussin.presentation.routeselection.RouteSelectionViewModel
import fi.danielz.hslbussin.presentation.theme.HSLBussinTheme

@Composable
fun RouteSelectionScreen() {
    val viewModel: RouteSelectionViewModel = viewModel()
    val routesState = viewModel.dataSource.routes.collectAsState(initial = emptyList())
    HSLBussinTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
        ) {
            items(routesState.value.size) {
                Text(text = routesState.value[it].name)
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    RouteSelectionScreen()
}