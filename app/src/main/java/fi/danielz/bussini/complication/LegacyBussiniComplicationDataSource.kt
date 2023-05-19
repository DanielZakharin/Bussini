package fi.danielz.bussini.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import fi.danielz.bussini.BuildConfig
import fi.danielz.bussini.StopQuery
import fi.danielz.bussini.network.NetworkStatus
import fi.danielz.bussini.network.di.apiUrl
import fi.danielz.bussini.network.queryAsNetworkResponse
import fi.danielz.bussini.preferences.PreferencesManager
import fi.danielz.bussini.preferences.SharedPreferencesManager
import fi.danielz.bussini.preferences.readRouteName
import fi.danielz.bussini.preferences.readStopAndPattern
import fi.danielz.bussini.presentation.stopdisplay.model.departureTime
import fi.danielz.bussini.utils.getSharedPrefs
import okhttp3.OkHttpClient
import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit


/**
 * Would be nice to use timeline data source, but to ensure compatibility, use just [SuspendingComplicationDataSourceService]
 * Sets a timed task for WorkManager inside onComplicationRequest to refresh data once its too old
 */
class LegacyBussiniComplicationDataSource : SuspendingComplicationDataSourceService() {
    private val okHttpClient = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()

    private val apolloClient = ApolloClient.Builder()
        .okHttpClient(okHttpClient)
        .serverUrl(apiUrl)
        .webSocketIdleTimeoutMillis(60000)
        .addHttpHeader("digitransit-subscription-key", BuildConfig.API_KEY)
        .build()

    override fun getPreviewData(type: ComplicationType): ComplicationData =
        buildCountdownComplication(
            "123",
            Instant.now(),
            ComplicationRequest(-1, type), // TODO fix jank
            baseContext
        )

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        Timber.d("Complication requested, type: ${request.complicationType.name}")

        val prefs: PreferencesManager = SharedPreferencesManager(
            getSharedPrefs()
        )


        val (stopId, patternId) = prefs.readStopAndPattern()
        val routeShortName = prefs.readRouteName()

        if (stopId == null || patternId == null) {
            Timber.d("No route selected, return buildNoRouteBussiniComplication")
            return buildNoRouteBussiniComplication(request, baseContext)
        }

        val res = apolloClient.queryAsNetworkResponse(StopQuery(stopId, patternId, 1))

        if (res is NetworkStatus.Error) {
            Timber.e("Apollo request failed while updating complication", res.error)
            // schedule a refresh attempt
            scheduleComplicationRefreshWork(applicationContext, Duration.ofMinutes(1))
            return buildErrorBussiniComplication(request, baseContext)
        } else if (res.body == null) {
            Timber.w("Apollo request result has no body\n $res")
            // schedule a refresh attempt, but for later
            scheduleComplicationRefreshWork(applicationContext, Duration.ofMinutes(10))
            return buildNoDeparturesBussiniComplication(request, baseContext)
        }

        val nextDeparture =
            res.body!!.stop?.stopTimesForPattern?.firstOrNull()?.departureTime

        if (nextDeparture == null) {
            Timber.d("No next departure found, returning error complication")
            Timber.d("Pattern $patternId, stop $stopId")
            // schedule an update in an hour
            scheduleComplicationRefreshWork(applicationContext, Duration.ofHours(1))
            return buildNoDeparturesBussiniComplication(request, baseContext)
        }

        val departureInstant = Instant.ofEpochMilli(nextDeparture)

        // set up work manager to refresh data on the departure time + 30 secs, to compensate for delays
        val durationToDeparture = Duration.between(Instant.now(), departureInstant.plusSeconds(30L))
        scheduleComplicationRefreshWork(applicationContext, durationToDeparture)

        return buildCountdownComplication(
            lineNumber = routeShortName,
            departureTime = departureInstant,
            complicationRequest = request,
            context = baseContext
        )
    }

}