package fi.danielz.hslbussin.presentation.stopselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.danielz.hslbussin.presentation.stopselection.model.StopsDataSource
import javax.inject.Inject

@HiltViewModel
class StopSelectionViewModel @Inject constructor(private val stopsDataSource: StopsDataSource) : ViewModel() {

    fun stops(patternGtfsId: String) = stopsDataSource.stopsForPatternId(patternGtfsId)

    val errors by lazy {
        stopsDataSource.errors
    }
}