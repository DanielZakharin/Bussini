package fi.danielz.bussini.presentation.stopdisplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.bussini.StopQuery
import fi.danielz.bussini.network.NetworkStatus
import fi.danielz.bussini.presentation.stopdisplay.compose.StopDisplayScreenUIState
import fi.danielz.bussini.presentation.stopdisplay.model.StopDeparturesDataSource
import fi.danielz.bussini.presentation.stopdisplay.model.StopDeparturesPattern
import fi.danielz.bussini.presentation.stopdisplay.model.StopSingleDepartureQueryData
import fi.danielz.bussini.presentation.stopdisplay.model.departureTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopDisplayViewModel @Inject constructor(
    application: Application,
    private val stopDeparturesDataSource: StopDeparturesDataSource
) :
    AndroidViewModel(application) {

    private lateinit var routenName: String

    private val stopDataPattern = MutableSharedFlow<StopDeparturesPattern>(replay = 1)
    private val departuresForStopAndPattern: StateFlow<NetworkStatus<StopQuery.Data>> =
        stopDataPattern.flatMapConcat { pattern ->
            stopDeparturesDataSource.stopDataForPattern(pattern)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetworkStatus.InProgress()
        )

    // local ticker that ticks down every minute
    val tickerFlow = flow {
        while (true) {
            delay(6000L)
            emit(System.currentTimeMillis()) // unique emission each time
        }
    }.shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        1
    )

    val uiState: Flow<StopDisplayScreenUIState> =
        departuresForStopAndPattern.map {
            when (it) {
                is NetworkStatus.InProgress -> StopDisplayScreenUIState.Loading()
                is NetworkStatus.Error -> StopDisplayScreenUIState.Error(
                    it.error
                )
                is NetworkStatus.Success -> {
                    val deps = it.body.stop?.stopTimesForPattern?.mapNotNull { stopTime ->
                        stopTime?.let(::StopSingleDepartureQueryData)
                    } ?: emptyList()
                    StopDisplayScreenUIState.Success(deps, routenName)
                }
            }
        }.distinctUntilChanged()

    private val tickerAndData = tickerFlow.combine(departuresForStopAndPattern, ::Pair).shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        1
    )

    fun reload() = stopDeparturesDataSource.reload()
    fun init(
        stopId: String,
        patternId: String,
        routenName: String
    ) {
        this.routenName = routenName
        stopDataPattern.tryEmit(StopDeparturesPattern(stopId, patternId))
        viewModelScope.launch {
            tickerAndData.collect { (currentTime, deps) ->
                val dep =
                    deps.body?.stop?.stopTimesForPattern?.firstOrNull()?.departureTime
                        ?: return@collect
                if (currentTime >= dep) {
                    stopDeparturesDataSource.reload()
                }
            }
        }
    }
}