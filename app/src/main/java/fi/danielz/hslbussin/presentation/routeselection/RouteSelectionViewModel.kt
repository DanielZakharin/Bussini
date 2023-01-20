package fi.danielz.hslbussin.presentation.routeselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    val dataSource: RoutesDataSource
) : ViewModel() {
    val routes by lazy {
        dataSource.routes
    }
    val errors by lazy {
        dataSource.errors
    }
    fun onRouteSelectedClick(route: RouteData) {
        Timber.d("Clicked on ${route.name}")
    }
}