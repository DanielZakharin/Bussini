package fi.danielz.hslbussin.presentation.stopdisplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData
import kotlinx.coroutines.CoroutineScope
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
    val departuresForStopAndPattern: Flow<List<StopSingleDepartureData>> by lazy {
        if (!initialized) throw IllegalStateException("StopViewModel not initialized!")
        stopIdFlow.combine(patternIdFlow, ::Pair).flatMapConcat { (stopId, patternId) ->
            if (stopId == null || patternId == null) return@flatMapConcat flow { }
            stopDeparturesDataSource.departuresForStopAndPattern(stopId, patternId)
        }
    }

    val errors: Flow<List<com.apollographql.apollo3.api.Error>?> by lazy {
        stopDeparturesDataSource.errors
    }

    val loading: StateFlow<Boolean> by lazy {
        departuresForStopAndPattern.combine(errors, ::Pair).map { (errors, departures) ->
            !errors.isNullOrEmpty() && !departures.isNullOrEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)
    }
}