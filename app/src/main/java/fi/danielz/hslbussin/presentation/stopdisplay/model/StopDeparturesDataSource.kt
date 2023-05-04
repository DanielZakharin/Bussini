package fi.danielz.hslbussin.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.di.AppCoroutineScope
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.network.queryAsNetworkResponseFlow
import fi.danielz.hslbussin.utils.millisToHoursMinutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

const val TICKER_DEFAULT_REFRESH_INTERVAL_MS = 30000L // five minutes
const val TICKER_DEFAULT_REFRESH_INTERVAL_MS_DEBUG = 300L // three seconds

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

data class StopDeparturesPattern(
    val stopId: String,
    val patternId: String
)

interface StopDeparturesDataSource {
    fun stopDataForPattern(
        pattern: StopDeparturesPattern
    ): Flow<NetworkStatus<StopQuery.Data>>

    fun reload()
}

/**
 * Provides stop departures via network request
 */
open class StopDeparturesNetworkDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) : StopDeparturesDataSource {

    private lateinit var latestPattern: StopDeparturesPattern
    protected val patternSharedFlow = MutableSharedFlow<StopDeparturesPattern>(replay = 1)
    protected open val networkResponse: Flow<NetworkStatus<StopQuery.Data>> =
        patternSharedFlow.flatMapConcat { pattern ->
            apolloClient.queryAsNetworkResponseFlow(StopQuery(pattern.stopId, pattern.patternId))
        }

    final override fun stopDataForPattern(pattern: StopDeparturesPattern): Flow<NetworkStatus<StopQuery.Data>> {
        latestPattern = pattern
        patternSharedFlow.tryEmit(pattern)
        return networkResponse
    }

    final override fun reload() {
        // dont reload if no pattern previously given
        if (!::latestPattern.isInitialized) return
        patternSharedFlow.tryEmit(latestPattern)
    }
}

class AutoRefreshingStopDeparturesNetworkDataSource @Inject constructor(
    apolloClient: ApolloClient,
    @AppCoroutineScope appCoroutineScope: CoroutineScope
) : StopDeparturesNetworkDataSource(apolloClient) {

    private val refreshDelay = MutableSharedFlow<Long>().apply {
        tryEmit(0L)
    } // no initial delay

    private val tickerFlow = refreshDelay.flatMapConcat { delayMillis ->
        flow {
            if (delayMillis > 0L) {
                delay(
                    delayMillis
                )
            }
            emit(System.currentTimeMillis())
        }
    }.shareIn(
        appCoroutineScope,
        SharingStarted.WhileSubscribed(),
        1
    )
    override val networkResponse: Flow<NetworkStatus<StopQuery.Data>> = tickerFlow.flatMapConcat {
        patternSharedFlow.flatMapConcat { pattern ->
            apolloClient.queryAsNetworkResponseFlow(StopQuery(pattern.stopId, pattern.patternId))
        }.map {
            val nextDepartureInMillis = it.body?.stop?.stopTimesForPattern?.firstOrNull()?.let {
                if (it.realtime == true) {
                    it.realtimeDeparture?.toLong()
                } else {
                    it.scheduledDeparture?.toLong()
                }
            } ?: TICKER_DEFAULT_REFRESH_INTERVAL_MS_DEBUG
            val delay = nextDepartureInMillis - System.currentTimeMillis()
            refreshDelay.tryEmit(delay)
            it
        }
    }

}
/*
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
}*/