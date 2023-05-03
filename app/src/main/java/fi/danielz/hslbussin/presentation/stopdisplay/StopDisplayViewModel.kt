package fi.danielz.hslbussin.presentation.stopdisplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.presentation.stopdisplay.compose.StopDisplayScreenUIState
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDisplayData
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureQueryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class StopDisplayViewModel @Inject constructor(
    application: Application,
    val stopDeparturesDataSource: StopDeparturesDataSource
) :
    AndroidViewModel(application) {

    private var initialized: Boolean = false
    private lateinit var routenName: String
    fun init(
        stopId: String,
        patternId: String,
        routenName: String
    ) {
        stopIdFlow.value = stopId
        patternIdFlow.value = patternId
        this.routenName = routenName
        initialized = true

        viewModelScope.launch {
            combine(tickerFlow, uiState) { latestTick, uiState ->
                if (uiState !is StopDisplayScreenUIState.Success) return@combine -1L
                val nextDep = uiState.departures.firstOrNull()?.timeOfDeparture
                if (nextDep != null && nextDep <= latestTick) {
                    System.currentTimeMillis()
                } else {
                    -1L
                }
            }.filter { it > 0L }.collect {
                tickerRefreshTrigger.emit(it)
            }
        }
    }

    private val tickerRefreshTrigger = MutableStateFlow(0L)
    private val stopIdFlow = MutableStateFlow<String?>(null)
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val combinedFlow = combine(stopIdFlow, patternIdFlow, tickerRefreshTrigger) { a, b, c ->
        Triple(
            a,
            b,
            c
        )
    }
    private val _departuresForStopAndPattern: Flow<NetworkStatus<StopQuery.Data>> by lazy {
        if (!initialized) throw IllegalStateException("StopViewModel not initialized!")
        combinedFlow.flatMapMerge { (stopId, patternId, refresh) ->
            if (refresh != 0L) Timber.d("Manual refresh triggered!")
            if (stopId == null || patternId == null) return@flatMapMerge flow { }
            stopDeparturesDataSource.stopDataForPattern(stopId, patternId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetworkStatus.InProgress()
        )
    }

    val tickerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(
                application.resources.getInteger(R.integer.global_ticker_frequency_millis)
                    .toLong()
            )
        }
    }


    val uiState: Flow<StopDisplayScreenUIState> by lazy {
        _departuresForStopAndPattern.map {
            when (it) {
                is NetworkStatus.InProgress -> StopDisplayScreenUIState.Loading()
                is NetworkStatus.Error -> StopDisplayScreenUIState.Error(
                    it.error ?: Exception("Np exception given...")
                )
                is NetworkStatus.Success -> {
                    val deps = it.body?.stop?.stopTimesForPattern?.mapNotNull { stopTime ->
                        stopTime?.let(::StopSingleDepartureQueryData)
                    } ?: emptyList()
                    StopDisplayScreenUIState.Success(deps ,routenName)
                }
            }
        }.distinctUntilChanged()
    }
}