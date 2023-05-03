package fi.danielz.hslbussin.presentation.routeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreenUIState
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesQueryData
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val dataSource: RoutesDataSource
) : ViewModel() {
    private val res = dataSource.routesNetwokrResponse

    val routeSelectionUIState =
        res.map {
            when (it) {

                is NetworkStatus.Success -> RouteSelectionScreenUIState.Success(
                    routes = it.responseBody.routes?.filterNotNull()?.map {
                        RoutesQueryData(it)
                    } ?: emptyList()
                )
                is NetworkStatus.Error -> RouteSelectionScreenUIState.Error(
                    listOf() // TODO figure out what form the errors should be
                )
                else -> RouteSelectionScreenUIState.Loading()
            }
        }
            .stateIn(
                scope = viewModelScope,
                initialValue = RouteSelectionScreenUIState.Loading(),
                started = WhileSubscribed(5000)
            )

    fun reloadRoutes() {
        dataSource.reload()
    }
}