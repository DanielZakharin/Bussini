package fi.danielz.hslbussin.presentation.stopselection.model

import com.apollographql.apollo3.ApolloClient
import fi.danielz.hslbussin.PatternQuery
import javax.inject.Inject
import com.apollographql.apollo3.api.Error
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
class StopsQueryData(queryDataItem: PatternQuery.Stop): StopData {
    override val gtfsId: String = queryDataItem.gtfsId
    override val name: String = queryDataItem.name
}

/**
 * Abstract datasource for stops
 * Fetches stop data from digitransit api
 */
interface StopsDataSource {
    fun stopsForPatternId(patternGtfsId: String): Flow<List<StopData>>
    val errors: Flow<List<Error>>
}

class StopsNetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) : StopsDataSource {
    private val patternIdFlow = MutableStateFlow<String?>(null)
    private val clientResult by lazy {
        patternIdFlow.flatMapConcat {
            it?.let {
                apolloClient.query(PatternQuery(it)).toFlow()
            } ?: flow {  }
        }
    }
    private val mappedStops by lazy {
        clientResult.mapNotNull {
            it.data?.pattern?.stops?.map {  stop ->
                StopsQueryData(stop)
            }
        }
    }
    override fun stopsForPatternId(patternGtfsId: String): Flow<List<StopData>> {
        patternIdFlow.value = patternGtfsId
        return mappedStops
    }

    override val errors: Flow<List<Error>> by lazy {
        clientResult.mapNotNull {
            it.errors
        }
    }

}