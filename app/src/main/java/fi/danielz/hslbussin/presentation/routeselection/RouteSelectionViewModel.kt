package fi.danielz.hslbussin.presentation.routeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreenUIState
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    dataSource: RoutesDataSource
) : ViewModel() {
    private val routes = dataSource.routes
    private val errors = dataSource.errors

    val routeSelectionUIState =
        combine(routes, errors) { routeData, errors ->
            when {
                routeData.isNotEmpty() -> RouteSelectionScreenUIState.Success(
                    routes = routeData
                )
                errors.isNotEmpty() -> RouteSelectionScreenUIState.Error(
                    errors = errors
                )
                else -> RouteSelectionScreenUIState.Loading()
            }
        }
            .stateIn(
                scope = viewModelScope,
                initialValue = RouteSelectionScreenUIState.Loading(),
                started = WhileSubscribed(5000)
            )
}