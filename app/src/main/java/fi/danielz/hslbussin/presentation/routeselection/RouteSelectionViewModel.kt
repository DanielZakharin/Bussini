package fi.danielz.hslbussin.presentation.routeselection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val dataSource: RoutesDataSource
) : ViewModel() {
    val routes by lazy {
        dataSource.routes
    }
}