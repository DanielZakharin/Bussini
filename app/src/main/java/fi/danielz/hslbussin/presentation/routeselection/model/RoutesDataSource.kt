package fi.danielz.hslbussin.presentation.routeselection.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.hslbussin.RoutesQuery
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.network.queryAsNetworkResponseFlow
import fi.danielz.hslbussin.presentation.directionselection.model.DirectionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import timber.log.Timber
import javax.inject.Inject

/**
 * Single simplified route to be presented in UI
 */
interface RouteData {
    val gtfsId: String
    val shortName: String
    val fullName: String
    val directions: List<DirectionData>?
}

/**
 * Wrapper for RoutesQuery.Route response that conforms to [RouteData]
 */
class RoutesQueryData(queryDataItem: RoutesQuery.Route) : RouteData {
    override val gtfsId: String = queryDataItem.gtfsId
    override val shortName: String = queryDataItem.shortName ?: ""
    override val fullName: String = "${queryDataItem.shortName} - ${queryDataItem.longName}"
    override val directions: List<DirectionData>? = queryDataItem.patterns?.mapNotNull {
        it?.let(::DirectionData)
    }
}

/**
 * Abstract datasource of [RouteData] objects
 */
interface RoutesDataSource {
    val routesNetwokrResponse: Flow<NetworkStatus<RoutesQuery.Data>>
    fun reload()
}

/**
 * Network datasource that conforms to [RoutesDataSource]
 * Fetches routes from digitransit api
 */
class RoutesNetworkDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
) : RoutesDataSource {
    private val networkRetryTrigger = MutableStateFlow(0)
    override val routesNetwokrResponse by lazy {
        networkRetryTrigger.flatMapConcat {
            Timber.d("Emitting reload trigger $it")
            apolloClient.queryAsNetworkResponseFlow(RoutesQuery())
        }
    }

    override fun reload() {
        networkRetryTrigger.tryEmit(networkRetryTrigger.value + 1)
    }
}