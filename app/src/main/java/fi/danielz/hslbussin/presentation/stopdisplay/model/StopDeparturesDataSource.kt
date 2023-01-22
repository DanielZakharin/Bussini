package fi.danielz.hslbussin.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.StopQuery
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

interface StopSingleDepartureData {
    val timeUntilDeparture: Long
    val displayText: String
}

class StopSingleDepartureQueryData(queryDataItem: StopQuery.StopTimesForPattern) :
    StopSingleDepartureData {

    /**
     * Departure time in millis, or -1L if parsing fails
     */
    override val timeUntilDeparture: Long = queryDataItem.let {
        // apollo types are all wonky? query types are all wrong...
        val serviceDay = (it.serviceDay as? Int)?.toLong() ?: return@let -1L
        val departureOffset: Long? =
            if (it.realtime == true) it.realtimeDeparture?.toLong() else it.scheduledDeparture?.toLong()
        departureOffset ?: return@let -1L
        ((serviceDay + departureOffset) * 1000) - System.currentTimeMillis() // convert to millis
    }

    override val displayText: String = timeUntilDeparture.takeIf {
        it != -1L
    }?.let {
        val timeInMinutes = it / 60000
        "$timeInMinutes min"
    } ?: "unknown"
}

interface StopDeparturesDataSource {
    fun departuresForStopAndPattern(
        stopId: String,
        patternId: String
    ): Flow<List<StopSingleDepartureData>>

    val errors: Flow<List<com.apollographql.apollo3.api.Error>?>
}

class StopDeparturesNetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) :
    StopDeparturesDataSource {

    private val stopIdFlow = MutableStateFlow<String?>(null)
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val stopDeparturesFlow: Flow<ApolloResponse<StopQuery.Data>> =
        stopIdFlow.combine(patternIdFlow, ::Pair).flatMapConcat { (stopId, patternId) ->
            if (stopId == null || patternId == null) return@flatMapConcat flow { }
            val res = apolloClient.query(StopQuery(stopId, patternId)).execute()
            Timber.d("res!! ${res.data}")
            apolloClient.query(StopQuery(stopId, patternId)).toFlow()
        }

    override fun departuresForStopAndPattern(
        stopId: String,
        patternId: String
    ): Flow<List<StopSingleDepartureData>> {
        stopIdFlow.value = stopId
        patternIdFlow.value = patternId
        return stopDeparturesFlow.mapNotNull {
            it.data?.stop?.stopTimesForPattern?.mapNotNull { stopTime ->
                stopTime?.let(::StopSingleDepartureQueryData)
            }
        }
    }

    override val errors: Flow<List<Error>?> by lazy {
        stopDeparturesFlow.map { it.errors }
    }
}