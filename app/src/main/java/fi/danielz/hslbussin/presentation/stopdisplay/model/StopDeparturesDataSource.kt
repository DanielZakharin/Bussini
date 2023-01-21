package fi.danielz.hslbussin.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.hslbussin.StopDeparturesQuery
import fi.danielz.hslbussin.StopQuery
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface StopSingleDepartureData {
    val departureTime: Long
}

class StopSingleDepartureQueryData(queryDataItem: StopDeparturesQuery.StopTimesForPattern) :
    StopSingleDepartureData {

    override val departureTime: Long = queryDataItem.let {
        //val departureOffset: Long = if(it.realtime == true) it.realtimeDeparture else it.scheduledDeparture
        // start of day + departure time
        //it.serviceDay + departureOffset
        //departureOffset
        // FIXME apollo types are all wonky?
        5L
    }
}

interface StopDeparturesDataSource {
    fun departuresForStopAndPattern(
        stopId: String,
        patternId: String
    ): Flow<List<StopSingleDepartureData>>

    val errors: Flow<List<com.apollographql.apollo3.api.Error>>
}

class StopDeparturesNetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) :
    StopDeparturesDataSource {

    private val stopIdFlow = MutableStateFlow<String?>(null)
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val stopDeparturesFlow =
        stopIdFlow.combine(patternIdFlow, ::Pair).flatMapConcat { (stopId, patternId) ->
            if (stopId == null || patternId == null) return@flatMapConcat flow { }
            apolloClient.query(StopQuery(stopId, patternId)).toFlow()
        }

    override fun departuresForStopAndPattern(
        stopId: String,
        patternId: String
    ): Flow<List<StopSingleDepartureData>> {
        stopIdFlow.value = stopId
        patternIdFlow.value = patternId
        return stopDeparturesFlow.mapNotNull {
        }
    }
}