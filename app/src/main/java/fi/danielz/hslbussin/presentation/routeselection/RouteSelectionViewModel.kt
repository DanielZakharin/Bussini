package fi.danielz.hslbussin.presentation.routeselection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    app: Application,
    val dataSource: RoutesDataSource
) : AndroidViewModel(app) {
    val routes by lazy {
        dataSource.routes
    }
    val errors by lazy {
        dataSource.errors
    }
}