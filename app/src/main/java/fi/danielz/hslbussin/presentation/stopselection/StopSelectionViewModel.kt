package fi.danielz.hslbussin.presentation.stopselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionScreenUIState
import fi.danielz.hslbussin.presentation.stopselection.model.StopsDataSource
import fi.danielz.hslbussin.presentation.stopselection.model.StopsQueryData
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StopSelectionViewModel @Inject constructor(private val stopsDataSource: StopsDataSource) :
    ViewModel() {


    private val patternGtfsId = MutableStateFlow("")

    private val stopsNetworkResult by lazy {
        patternGtfsId.flatMapConcat { gtfsId ->
            stopsDataSource.stopsForPatternId(gtfsId)
        }
    }

    val uiState: Flow<StopSelectionScreenUIState> by lazy {
        stopsNetworkResult.map {
            when (it) {
                is NetworkStatus.Error -> {
                    StopSelectionScreenUIState.Error(emptyList()) // TODO change to Exception from list
                }
                is NetworkStatus.InProgress -> {
                    StopSelectionScreenUIState.Loading()
                }
                is NetworkStatus.Success -> {
                    val mappedStops = it.body.pattern?.stops?.map { stop ->
                        StopsQueryData(stop)
                    } ?: emptyList()
                    StopSelectionScreenUIState.Success(mappedStops)
                }
            }
        }
            .stateIn(
                scope = viewModelScope,
                initialValue = StopSelectionScreenUIState.Loading(),
                started = SharingStarted.WhileSubscribed(5000)
            )
    }

    fun setPatternGtfsId(patternGtfsId: String) {
        this.patternGtfsId.value = patternGtfsId
    }
    fun reload() = stopsDataSource.reload()
}
