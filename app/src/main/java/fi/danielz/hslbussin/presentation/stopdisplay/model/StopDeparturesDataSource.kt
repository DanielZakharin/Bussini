package fi.danielz.hslbussin.presentation.stopdisplay.model

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.di.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.Duration
import javax.inject.Inject

data class StopDisplayData(
    val routeName: String,
    val departures: List<StopSingleDepartureData>
) {
    companion object {
        val empty = StopDisplayData("", emptyList())
    }
}

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
        millisToHoursMinutes(it)
    } ?: "unknown"

    companion object {
        // internal companion object to be able to be testable
        // TODO consider moving outside this class
        internal fun millisToHoursMinutes(millis: Long): String {
            val duration = Duration.ofMillis(millis)
            val hoursPart = duration.toHours()
            val minutesPart = duration.minusHours(hoursPart).toMinutes().toInt()
            return if (hoursPart > 0L) {
                "${hoursPart}h ${minutesPart}min"
            } else {
                "${minutesPart}min"
            }
        }
    }
}

interface StopDeparturesDataSource {
    fun stopDataForPattern(
        stopId: String,
        patternId: String
    ): Flow<StopDisplayData>

    val errors: Flow<List<Error>>
}

class StopDeparturesNetworkDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    @AppCoroutineScope appCoroutineScope: CoroutineScope
) :
    StopDeparturesDataSource {

    private val stopIdFlow = MutableStateFlow("")
    private val patternIdFlow = MutableStateFlow("")
    private val departuresResult: Flow<ApolloResponse<StopQuery.Data>> by lazy {
        stopIdFlow.combine(patternIdFlow) { stopId, patternId ->
            val res = apolloClient.query(StopQuery(stopId, patternId)).execute()
            Timber.d("res!! ${res.data}")
            apolloClient.query(StopQuery(stopId, patternId)).execute()
        }
    }
    private val stopDataFlow: Flow<StopDisplayData> by lazy {
        departuresResult.map {
            StopDisplayData(
                routeName = "FIXME" ?: "",
                departures = it.data?.stop?.stopTimesForPattern?.mapNotNull { stopTime ->
                    stopTime?.let(::StopSingleDepartureQueryData)
                } ?: emptyList())
        }.stateIn(
            started = SharingStarted.WhileSubscribed(5000),
            scope = appCoroutineScope,
            initialValue = StopDisplayData.empty
        )
    }

    override fun stopDataForPattern(
        stopId: String,
        patternId: String
    ): Flow<StopDisplayData> {
        stopIdFlow.value = stopId
        patternIdFlow.value = patternId
        return stopDataFlow
    }

    override val errors: Flow<List<Error>> by lazy {
        departuresResult.mapNotNull { it.errors }.stateIn(
            scope = appCoroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}