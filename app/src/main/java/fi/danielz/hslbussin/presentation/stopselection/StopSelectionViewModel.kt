package fi.danielz.hslbussin.presentation.stopselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionScreenUIState
import fi.danielz.hslbussin.presentation.stopselection.model.StopsDataSource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StopSelectionViewModel @Inject constructor(private val stopsDataSource: StopsDataSource) :
    ViewModel() {


    private val patternGtfsId = MutableStateFlow("")
    fun setPatternGtfsId(patternGtfsId: String) {
        this.patternGtfsId.value = patternGtfsId
    }

    private val stops by lazy {
        patternGtfsId.flatMapConcat { gtfsId ->
            stopsDataSource.stopsForPatternId(gtfsId)
        }
    }

    private val errors by lazy {
        stopsDataSource.errors
    }

    val uiState: Flow<StopSelectionScreenUIState> by lazy {
        combine(stops, errors) { stopsData, errors ->
            when {
                stopsData.isNotEmpty() -> StopSelectionScreenUIState.Success(
                    stops = stopsData,
                    "Select Stop"
                )
                errors.isNotEmpty() -> StopSelectionScreenUIState.Error(
                    errors = errors
                )
                else -> StopSelectionScreenUIState.Loading()
            }
        }
            .stateIn(
                scope = viewModelScope,
                initialValue = StopSelectionScreenUIState.Loading(),
                started = SharingStarted.WhileSubscribed(5000)
            )
    }
}
