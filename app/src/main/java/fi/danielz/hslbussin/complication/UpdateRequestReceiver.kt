package fi.danielz.hslbussin.complication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import timber.log.Timber

class UpdateRequestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }
}

// https://github.com/googlecodelabs/complications-data-source/blob/master/complete/src/main/java/com/example/android/wearable/complicationsdatasource/CustomComplicationDataSourceService.kt
class BussiniComplicationProviderService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = "85:\n12min").build(),
            contentDescription = PlainComplicationText.Builder(text = "Short Text version of Number.")
                .build()
        ).build()
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        // Create Tap Action so that the user can trigger an update by tapping the complication.
        //val thisDataSource = ComponentName(this, javaClass)

        Timber.d("Complication request receive at ${System.currentTimeMillis()}")
/*
        val prefs: PreferencesManager = SharedPreferencesManager(
            getSharedPrefs()
        )

        val (stopId, patternId) = prefs.readStopAndPattern()
        val routeShortName = prefs.readRouteName()

        if (stopId == null || patternId == null) return buildComplication(
            "No route selected",
            request.complicationType
        )

        Timber.d("DEBUG $stopId $patternId $routeShortName")

        val res = apolloClient.query(StopQuery(stopId, patternId)).execute() // bit iffy with the apollo client here...
        Timber.d("Complication res ${res.data}")
        val nextDeparture: Int = res.data?.stop?.stopTimesForPattern?.firstOrNull()?.let {
            if (it.realtime == true) {
                it.realtimeDeparture
            } else {
                it.scheduledDeparture
            }
        }
            ?: return buildComplication(
                "No departures found",
                request.complicationType
            )

        val formattedDeparture = millisToHoursMinutes(nextDeparture.toLong())

        return buildComplication("$routeShortName: $formattedDeparture", request.complicationType)
        */
        return null
    }
}

private fun buildComplication(displayText: String, complicationType: ComplicationType) =
    when (complicationType) {

        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = displayText).build(),
            contentDescription = PlainComplicationText
                .Builder(text = "Short Text version of Number.").build()
        )
            //.setTapAction(complicationPendingIntent)
            .build()

        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = displayText).build(),
            contentDescription = PlainComplicationText
                .Builder(text = "Long Text version of Number.").build()
        )
            //.setTapAction(complicationPendingIntent)
            .build()

        else -> {
            Timber.w("Unexpected complication type $complicationType")
            null
        }
    }