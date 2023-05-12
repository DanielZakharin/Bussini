package fi.danielz.hslbussin.complication

import android.content.ComponentName
import android.content.Context
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import androidx.work.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import fi.danielz.hslbussin.BuildConfig
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.network.NetworkStatus
import fi.danielz.hslbussin.network.di.apiUrl
import fi.danielz.hslbussin.network.queryAsNetworkResponse
import fi.danielz.hslbussin.preferences.PreferencesManager
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.readRouteName
import fi.danielz.hslbussin.preferences.readStopAndPattern
import fi.danielz.hslbussin.presentation.stopdisplay.model.departureTime
import fi.danielz.hslbussin.utils.getSharedPrefs
import okhttp3.OkHttpClient
import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit


class ComplicationDataRefresherWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Timber.i("ComplicationDataRefresherWorker working to refresh complication data")
        return try {
            requestComplicationUpdate(appContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure(Data.Builder().putAll(mapOf(ERROR_KEY to e)).build())
        }
    }

    companion object {
        const val ERROR_KEY = "ComplicationDataRefresherWorker_ERROR_KEY"
    }
}

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

    override fun getPreviewData(type: ComplicationType): ComplicationData = buildDefaultBussiniComplication(
        "123",
        Instant.now(),
        ComplicationRequest(-1, type) // TODO fix jank
    )

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {

        val prefs: PreferencesManager = SharedPreferencesManager(
            getSharedPrefs()
        )

        val (stopId, patternId) = prefs.readStopAndPattern()
        val routeShortName = prefs.readRouteName()

        if (stopId == null || patternId == null) return buildNoRouteBussiniComplication(request)

        val res = apolloClient.queryAsNetworkResponse(StopQuery(stopId, patternId, 1))

        if(res is NetworkStatus.Error) {
            Timber.e("Apollo request failed while updating complication", res.error)
            // schedule a refresh attempt
            scheduleComplicationRefreshWork(Duration.ofMinutes(1))
            return buildErrorBussiniComplication(request)
        } else if (res.body == null) {
            Timber.w("Apollo request result has no body\n $res")
            // schedule a refresh attempt
            scheduleComplicationRefreshWork(Duration.ofMinutes(1))
            return buildErrorBussiniComplication(request)
        }

        val nextDeparture =
            res.body!!.stop?.stopTimesForPattern?.firstOrNull()?.departureTime
                ?: return null

        val departureInstant = Instant.ofEpochMilli(nextDeparture)

        // set up work manager to refresh data on the departure time + 30 secs, to compensate for delays
        val durationToDeparture = Duration.between(Instant.now(), departureInstant.plusSeconds(30L))
        scheduleComplicationRefreshWork(durationToDeparture)

        return buildDefaultBussiniComplication(
            lineNumber = routeShortName,
            departureTime = departureInstant,
            complicationRequest = request
        )
    }

    private fun scheduleComplicationRefreshWork(delay: Duration) {
        val complicationDataRefreshWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<ComplicationDataRefresherWorker>()
                .setInitialDelay(delay)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(complicationDataRefreshWorkRequest)

    }

}