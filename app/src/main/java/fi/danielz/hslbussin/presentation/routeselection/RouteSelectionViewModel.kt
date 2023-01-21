package fi.danielz.hslbussin.presentation.routeselection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val dataSource: RoutesDataSource
) : ViewModel() {
    val routes by lazy {
        dataSource.routes
    }
    val errors by lazy {
        dataSource.errors
    }
}