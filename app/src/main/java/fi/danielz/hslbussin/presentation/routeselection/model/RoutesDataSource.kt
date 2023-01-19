package fi.danielz.hslbussin.presentation.routeselection.model

import com.apollographql.apollo3.ApolloClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import fi.danielz.hslbussin.RoutesQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

/**
 * Single simplified route to be presented in UI
 */
interface RouteData {
    val gtfsId: String
    val name: String
}

/**
 * Wrapper for RoutesQuery.Route response that conforms to [RouteData]
 */
class RoutesQueryData(queryDataItem: RoutesQuery.Route) : RouteData {
    override val gtfsId: String = queryDataItem.gtfsId
    override val name: String = queryDataItem.shortName ?: ""
}

/**
 * Abstract datasource of [RouteData] objects
 */
interface RoutesDataSource {
    val routes: Flow<List<RouteData>>
}

/**
 * Network datasource that conforms to [RoutesDataSource]
 * Fetches routes from digitransit api
 */
class RoutesNetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) :
    RoutesDataSource {
    override val routes: Flow<List<RouteData>> by lazy {
        apolloClient.query(RoutesQuery()).toFlow().mapNotNull {
            it.data?.routes?.filterNotNull()?.map {
                RoutesQueryData(it)
            }
        }
    }
}