package fi.danielz.hslbussin.presentation.stopdisplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.stopdisplay.compose.StopDisplayScreenUIState
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDisplayData
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StopDisplayViewModel @Inject constructor(private val stopDeparturesDataSource: StopDeparturesDataSource) :
    ViewModel() {

    private var initialized: Boolean = false
    fun init(
        stopId: String,
        patternId: String
    ) {
        stopIdFlow.value = stopId
        patternIdFlow.value = patternId
        initialized = true
    }

    private val stopIdFlow = MutableStateFlow<String?>(null)
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val _departuresForStopAndPattern: Flow<StopDisplayData> by lazy {
        if (!initialized) throw IllegalStateException("StopViewModel not initialized!")
        stopIdFlow.combine(patternIdFlow, ::Pair).flatMapConcat { (stopId, patternId) ->
            if (stopId == null || patternId == null) return@flatMapConcat flow { }
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

    val uiState: Flow<StopDisplayScreenUIState> by lazy {
        _departuresForStopAndPattern.combine(errors) { stopData, errors ->
            when {
                errors.isNotEmpty() -> StopDisplayScreenUIState.Error(errors)
                stopData.departures.isNotEmpty() -> StopDisplayScreenUIState.Success(
                    stopData.departures,
                    stopData.routeName
                )
                else -> StopDisplayScreenUIState.Loading()
            }
        }
    }
}