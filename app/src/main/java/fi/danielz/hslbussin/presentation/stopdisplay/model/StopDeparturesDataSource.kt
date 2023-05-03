package fi.danielz.hslbussin.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.di.AppCoroutineScope
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.network.queryAsNetworkResponseFlow
import fi.danielz.hslbussin.utils.millisToHoursMinutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StopDisplayData(
    val departures: List<StopSingleDepartureData>
) {
    companion object {
        val empty = StopDisplayData(emptyList())
    }
}

interface StopSingleDepartureData {
    val timeOfDeparture: Long
    fun timeUntilDeparture(fromTimePoint: Long): Long
    fun displayText(fromTimePoint: Long): String
}

class StopSingleDepartureQueryData(private val queryDataItem: StopQuery.StopTimesForPattern) :
    StopSingleDepartureData {

    override val timeOfDeparture: Long
        get() = queryDataItem.let {
            // apollo types are all wonky? query types are all wrong...
            val serviceDay = (it.serviceDay as? Int)?.toLong() ?: return@let -1L
            val departureOffset: Long? =
                if (it.realtime == true) it.realtimeDeparture?.toLong() else it.scheduledDeparture?.toLong()
            departureOffset ?: return@let -1L
            ((serviceDay + departureOffset) * 1000)
        }

    /**
     * Departure time in millis, or -1L if parsing fails
     */
    override fun timeUntilDeparture(fromTimePoint: Long): Long =
        timeOfDeparture - fromTimePoint

    override fun displayText(fromTimePoint: Long): String =
        millisToHoursMinutes(timeUntilDeparture(fromTimePoint))
}

interface StopDeparturesDataSource {
    fun stopDataForPattern(
        stopId: String,
        patternId: String
    ): Flow<NetworkStatus<StopQuery.Data>>

    fun reload()
}

class StopDeparturesNetworkDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    @AppCoroutineScope appCoroutineScope: CoroutineScope
) :
    StopDeparturesDataSource {

    private val networkRetryTrigger = MutableStateFlow(0)

    private val stopIdFlow = MutableSharedFlow<String>(1)
    private val patternIdFlow = MutableSharedFlow<String>(1)
    private val departuresResult: Flow<NetworkStatus<StopQuery.Data>> by lazy {
        networkRetryTrigger.flatMapConcat {
            stopIdFlow.combineTransform(patternIdFlow) { stopId, patternId ->
                apolloClient.queryAsNetworkResponseFlow(StopQuery(stopId, patternId))
            }
        }
    }
    private val stopDataFlow: Flow<NetworkStatus<StopQuery.Data>> by lazy {
        departuresResult.stateIn(
            started = SharingStarted.WhileSubscribed(5000),
            scope = appCoroutineScope,
            initialValue = NetworkStatus.InProgress()
        )
    }

    override fun stopDataForPattern(
        stopId: String,
        patternId: String
    ): Flow<NetworkStatus<StopQuery.Data>> {
        stopIdFlow.tryEmit(stopId)
        patternIdFlow.tryEmit(patternId)
        return stopDataFlow
    }

    override fun reload() {
        networkRetryTrigger.tryEmit(networkRetryTrigger.value + 1)
    }
}