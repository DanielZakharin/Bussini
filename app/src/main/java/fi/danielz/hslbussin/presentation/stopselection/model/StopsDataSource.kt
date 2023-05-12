package fi.danielz.hslbussin.presentation.stopselection.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.hslbussin.PatternQuery
import javax.inject.Inject
import fi.danielz.hslbussin.di.AppCoroutineScope
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.network.queryAsNetworkResponseFlow
import fi.danielz.hslbussin.presentation.shared.ReloadableDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*


/**
 * Single simplified pattern to be presented to UI
 */
interface StopData {
    val gtfsId: String
    val name: String
}

/**
 * Wrapper class for PatternQuery.Pattern.Stop that conforms to [StopData]
 */
class StopsQueryData(queryDataItem: PatternQuery.Stop) : StopData {
    override val gtfsId: String = queryDataItem.gtfsId
    override val name: String = queryDataItem.name
}

/**
 * Abstract datasource for stops
 * Fetches stop data from digitransit api
 */
interface StopsDataSource : ReloadableDataSource {
    fun stopsForPatternId(patternGtfsId: String): Flow<NetworkStatus<PatternQuery.Data>>
}

class StopsNetworkDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) : StopsDataSource {
    private val networkRetryTrigger = MutableStateFlow(0)
    private val patternIdFlow = MutableSharedFlow<String>(replay = 1)
    private val stopsNetworkResponse: Flow<NetworkStatus<PatternQuery.Data>> by lazy {
        combine(patternIdFlow, networkRetryTrigger, ::Pair).flatMapConcat { (pattern, _) ->
            apolloClient.queryAsNetworkResponseFlow(PatternQuery(pattern))
        }
    }

    override fun stopsForPatternId(patternGtfsId: String): Flow<NetworkStatus<PatternQuery.Data>> {
        patternIdFlow.tryEmit(patternGtfsId)
        return stopsNetworkResponse
    }

    override fun reload() {
        networkRetryTrigger.tryEmit(networkRetryTrigger.value + 1)
    }

}