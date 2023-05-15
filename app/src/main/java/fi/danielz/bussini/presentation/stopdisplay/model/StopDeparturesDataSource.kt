package fi.danielz.bussini.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.bussini.StopQuery
import fi.danielz.bussini.network.NetworkStatus
import fi.danielz.bussini.network.queryAsNetworkResponseFlow
import fi.danielz.bussini.utils.millisToHoursMinutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

val StopQuery.StopTimesForPattern.departureTime: Long
    get() {
        return ((if (realtime == true) realtimeDeparture else scheduledDeparture)?.toLong()
            ?: 0L) * 1000 + ((serviceDay as Int).toLong() * 1000)
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

    private val numberOfDepartures = 10

    private lateinit var latestPattern: StopDeparturesPattern
    private val patternSharedFlow = MutableSharedFlow<StopDeparturesPattern>(replay = 1)
    protected open val networkResponse: Flow<NetworkStatus<StopQuery.Data>> =
        patternSharedFlow.flatMapConcat { pattern ->
            apolloClient.queryAsNetworkResponseFlow(
                StopQuery(
                    pattern.stopId,
                    pattern.patternId,
                    numberOfDepartures
                )
            )
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
