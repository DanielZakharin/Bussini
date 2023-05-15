package fi.danielz.bussini.presentation.routeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.bussini.network.NetworkStatus
import fi.danielz.bussini.presentation.routeselection.compose.RouteSelectionScreenUIState
import fi.danielz.bussini.presentation.routeselection.model.RoutesDataSource
import fi.danielz.bussini.presentation.routeselection.model.RoutesQueryData
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
            when (val status = it) {

                is NetworkStatus.Success -> RouteSelectionScreenUIState.Success(
                    routes = status.responseBody.routes?.filterNotNull()?.map { route ->
                        RoutesQueryData(route)
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