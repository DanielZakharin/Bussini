package fi.danielz.hslbussin.complication

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import timber.log.Timber
import java.util.*

class UpdateRequestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }
}

// https://github.com/googlecodelabs/complications-data-source/blob/master/complete/src/main/java/com/example/android/wearable/complicationsdatasource/CustomComplicationDataSourceService.kt
class BussiniComplicationProviderService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: androidx.wear.watchface.complications.data.ComplicationType): androidx.wear.watchface.complications.data.ComplicationData? {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = "85:\n12min").build(),
            contentDescription = PlainComplicationText.Builder(text = "Short Text version of Number.")
                .build()
        ).build()
    }

    override suspend fun onComplicationRequest(request: androidx.wear.watchface.complications.datasource.ComplicationRequest): androidx.wear.watchface.complications.data.ComplicationData? {
        // Create Tap Action so that the user can trigger an update by tapping the complication.
        //val thisDataSource = ComponentName(this, javaClass)

        // Retrieves your data, in this case, we grab an incrementing number from Datastore.
        val number: Int = 1337 /* TODO plug in network request = applicationContext.dataStore.data
            .map { preferences ->
                preferences[TAP_COUNTER_PREF_KEY] ?: 0
            }
            .first()*/

        val numberText = String.format(Locale.getDefault(), "%d!", number)

        return when (request.complicationType) {

            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = numberText).build(),
                contentDescription = PlainComplicationText
                    .Builder(text = "Short Text version of Number.").build()
            )
                //.setTapAction(complicationPendingIntent)
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = "Number: $numberText").build(),
                contentDescription = PlainComplicationText
                    .Builder(text = "Long Text version of Number.").build()
            )
                //.setTapAction(complicationPendingIntent)
                .build()

            ComplicationType.RANGED_VALUE -> RangedValueComplicationData.Builder(
                value = number.toFloat(),
                min = 0f,
                max = 9001f,//ComplicationTapBroadcastReceiver.MAX_NUMBER.toFloat(),
                contentDescription = PlainComplicationText
                    .Builder(text = "Ranged Value version of Number.").build()
            )
                .setText(PlainComplicationText.Builder(text = numberText).build())
                //.setTapAction(complicationPendingIntent)
                .build()

            else -> {
                Timber.w("Unexpected complication type " + request.complicationType)
                null
            }
        }
    }
}