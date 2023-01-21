package fi.danielz.hslbussin.presentation.routeselection.model

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.RoutesQuery
import fi.danielz.hslbussin.presentation.directionselection.model.DirectionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

/**
 * Single simplified route to be presented in UI
 */
interface RouteData {
    val gtfsId: String
    val name: String
    val directions: List<DirectionData>?
}

/**
 * Wrapper for RoutesQuery.Route response that conforms to [RouteData]
 */
class RoutesQueryData(queryDataItem: RoutesQuery.Route) : RouteData {
    override val gtfsId: String = queryDataItem.gtfsId
    override val name: String = "${queryDataItem.shortName} - ${queryDataItem.longName}"
    override val directions: List<DirectionData>? = queryDataItem.patterns?.mapNotNull {
        it?.let(::DirectionData)
    }
}

/**
 * Abstract datasource of [RouteData] objects
 */
interface RoutesDataSource {
    val routes: Flow<List<RouteData>>
    val errors: Flow<List<Error>>
}

/**
 * Network datasource that conforms to [RoutesDataSource]
 * Fetches routes from digitransit api
 */
class RoutesNetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) :
    RoutesDataSource {
    private val clientResult by lazy {
        apolloClient.query(RoutesQuery()).toFlow()
    }
    override val routes: Flow<List<RouteData>> by lazy {
        clientResult.mapNotNull {
            it.data?.routes?.filterNotNull()?.map {
                RoutesQueryData(it)
            }
        }
    }
    override val errors: Flow<List<Error>> by lazy {
        clientResult.mapNotNull {
            it.errors
        }
    }
}