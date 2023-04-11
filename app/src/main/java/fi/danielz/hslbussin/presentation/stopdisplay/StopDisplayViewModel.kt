package fi.danielz.hslbussin.presentation.stopdisplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.presentation.stopdisplay.compose.StopDisplayScreenUIState
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDisplayData
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
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
                manualRefresh.emit(it)
            }
        }
    }

    private val manualRefresh = MutableStateFlow(0L)
    private val stopIdFlow = MutableStateFlow<String?>(null)
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val combinedFlow = combine(stopIdFlow, patternIdFlow, manualRefresh) { a, b, c ->
        Triple(
            a,
            b,
            c
        )
    }
    private val _departuresForStopAndPattern: Flow<StopDisplayData> by lazy {
        if (!initialized) throw IllegalStateException("StopViewModel not initialized!")
        combinedFlow.flatMapMerge { (stopId, patternId, refresh) ->
            if (refresh != 0L) Timber.d("Manual refresh triggered!")
            if (stopId == null || patternId == null) return@flatMapMerge flow { }
            stopDeparturesDataSource.stopDataForPattern(stopId, patternId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StopDisplayData.empty
        )
    }

    private val errors: Flow<List<com.apollographql.apollo3.api.Error>> by lazy {
        stopDeparturesDataSource.errors
    }

    val tickerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(
                application.resources.getInteger(R.integer.global_ticker_frequency_millis_debug)
                    .toLong()
            )
        }
    }


    val uiState: Flow<StopDisplayScreenUIState> by lazy {
        combine(_departuresForStopAndPattern, errors) { stopData, errors ->
            when {
                errors.isNotEmpty() -> StopDisplayScreenUIState.Error(errors)
                stopData.departures.isNotEmpty() -> StopDisplayScreenUIState.Success(
                    stopData.departures,
                    routenName
                )
                else -> StopDisplayScreenUIState.Loading()
            }
        }.distinctUntilChanged()
    }
}