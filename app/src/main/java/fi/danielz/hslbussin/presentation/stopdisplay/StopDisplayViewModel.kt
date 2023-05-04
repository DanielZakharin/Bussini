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
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesPattern
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

    private lateinit var routenName: String
    fun init(
        stopId: String,
        patternId: String,
        routenName: String
    ) {
        this.routenName = routenName
        stopDataPattern.tryEmit(StopDeparturesPattern(stopId, patternId))
    }

    private val stopDataPattern = MutableSharedFlow<StopDeparturesPattern>()
    private val _departuresForStopAndPattern: Flow<NetworkStatus<StopQuery.Data>> =
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
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        System.currentTimeMillis()
    )

    val uiState: Flow<StopDisplayScreenUIState> =
        _departuresForStopAndPattern.map {
            when (it) {
                is NetworkStatus.InProgress -> StopDisplayScreenUIState.Loading()
                is NetworkStatus.Error -> StopDisplayScreenUIState.Error(
                    it.error ?: Exception("No exception given...")
                )
                is NetworkStatus.Success -> {
                    val deps = it.body?.stop?.stopTimesForPattern?.mapNotNull { stopTime ->
                        stopTime?.let(::StopSingleDepartureQueryData)
                    } ?: emptyList()
                    StopDisplayScreenUIState.Success(deps, routenName)
                }
            }
        }.distinctUntilChanged()
}