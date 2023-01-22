package fi.danielz.hslbussin.presentation.stopdisplay

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val _departuresForStopAndPattern: Flow<List<StopSingleDepartureData>> by lazy {
        if (!initialized) throw IllegalStateException("StopViewModel not initialized!")
        stopIdFlow.combine(patternIdFlow, ::Pair).flatMapConcat { (stopId, patternId) ->
            if (stopId == null || patternId == null) return@flatMapConcat flow { }
            stopDeparturesDataSource.departuresForStopAndPattern(stopId, patternId)
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)
    }
    val nextDepartureText: StateFlow<String?> by lazy {
        _departuresForStopAndPattern.map {
            it.firstOrNull()?.let { firstDeparture ->
                "Next bus in ${firstDeparture.displayText}"
            } ?: "No departures within 24h"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }

    val subSequentDeparturesHeader: StateFlow<String?> by lazy {
        _departuresForStopAndPattern.map {
            if (it.isNotEmpty()) "Other departures:" else null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }

    val subsequentDepartures: Flow<List<StopSingleDepartureData>> by lazy {
        _departuresForStopAndPattern.map {
            it.toMutableList().apply {
                // avoid NoSuchElementException
                if (isNotEmpty()) {
                    removeFirst()
                }
            }
        }
    }

    val errors: Flow<List<com.apollographql.apollo3.api.Error>?> by lazy {
        stopDeparturesDataSource.errors
    }

    val loading: StateFlow<Boolean> by lazy {
        _departuresForStopAndPattern.combine(errors, ::Pair).map { (errors, departures) ->
            !errors.isNullOrEmpty() && !departures.isNullOrEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)
    }
}