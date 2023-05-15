package fi.danielz.hslbussin.complication

import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import fi.danielz.hslbussin.BuildConfig
import fi.danielz.hslbussin.StopQuery
import fi.danielz.hslbussin.network.di.apiUrl
import fi.danielz.hslbussin.preferences.PreferencesManager
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.readRouteName
import fi.danielz.hslbussin.preferences.readStopAndPattern
import fi.danielz.hslbussin.presentation.stopdisplay.model.departureTime
import fi.danielz.hslbussin.utils.getSharedPrefs
import fi.danielz.hslbussin.utils.millisToHoursMinutes
import okhttp3.OkHttpClient
import timber.log.Timber
import java.time.Instant

// https://github.com/googlecodelabs/complications-data-source/blob/master/complete/src/main/java/com/example/android/wearable/complicationsdatasource/CustomComplicationDataSourceService.kt
class BussiniComplicationDataSource : SuspendingTimelineComplicationDataSourceService() {
    private val okHttpClient = OkHttpClient.Builder().build()

    private val apolloClient = ApolloClient.Builder()
        .okHttpClient(okHttpClient)
        .serverUrl(apiUrl)
        .addHttpHeader("digitransit-subscription-key", BuildConfig.API_KEY)
        .build()

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = "85:\n12min").build(),
            contentDescription = PlainComplicationText.Builder(text = "Short Text version of Number.")
                .build()
        ).build()
    }


    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationDataTimeline? {

        Timber.d("Complication request receive at ${System.currentTimeMillis()}")
        return debugTimeline(request)

        val prefs: PreferencesManager = SharedPreferencesManager(
            getSharedPrefs()
        )

        val (stopId, patternId) = prefs.readStopAndPattern()
        val routeShortName = prefs.readRouteName()

        if (stopId == null || patternId == null) return null

        Timber.d("DEBUG $stopId $patternId $routeShortName")

        val res = apolloClient.query(StopQuery(stopId, patternId, 1))
            .execute() // bit iffy with the apollo client here...
        Timber.d("Complication res ${res.data}")
        val nextDeparture: Long =
            res.data?.stop?.stopTimesForPattern?.firstOrNull()?.departureTime ?: return null

        val departure = Instant.ofEpochMilli(nextDeparture)
        val timelineEntries = res.data?.stop?.stopTimesForPattern?.asSequence()?.map {
            it?.departureTime
        }?.filterNotNull()?.map {
            val formattedDepartureTime = millisToHoursMinutes(it.toLong())
            TimelineEntry(
                validity = TimeInterval(Instant.now(), Instant.ofEpochMilli(it.toLong())),
                complicationData = ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = formattedDepartureTime).build(),
                    contentDescription = PlainComplicationText
                        .Builder(text = "Short Text version of Number.").build()
                )
                    //.setTapAction(complicationPendingIntent)
                    .build()
            )
        }?.toList() ?: emptyList()

        val timeLine = ComplicationDataTimeline(
            defaultComplicationData = buildCountdownComplication(
                "$routeShortName",
                departure,
                request
            ),
            timelineEntries = timelineEntries
        )

        return timeLine
    }


}


fun debugTimeline(request: ComplicationRequest): ComplicationDataTimeline {
    var validityStart = Instant.now()
    val timelineEntries = listOf(1, 2, 3, 4, 5).map { num ->
        val departure = validityStart.plusSeconds(60)
        val validity = TimeInterval(validityStart, departure)
        validityStart = departure

        TimelineEntry(
            validity,
            buildCountdownComplication("N $num", departure, request)
        )
    }

    timelineEntries.let { entries ->
        Timber.d(
            "New instance of debug timeline at ${System.currentTimeMillis()} \n Entries:" +
                    " ${entries.mapIndexed { i, e -> "\nTimelineEntry $i:\n  | Start: ${e.validity.start}\n  | End: ${e.validity.end}" }}"
        )
    }
    return ComplicationDataTimeline(
        defaultComplicationData = buildCountdownComplication(
            "Def",
            Instant.now().plusSeconds(50),
            request
        ),
        timelineEntries
    )
}
