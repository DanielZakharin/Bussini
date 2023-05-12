package fi.danielz.hslbussin.complication

import android.content.ComponentName
import android.content.Context
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import timber.log.Timber
import java.time.Instant

internal fun buildDefaultBussiniComplication(
    lineNumber: String,
    departureTime: Instant,
    complicationRequest: ComplicationRequest
) =
    when (complicationRequest.complicationType) {

        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = TimeDifferenceComplicationText.Builder(
                TimeDifferenceStyle.SHORT_DUAL_UNIT,
                CountDownTimeReference(departureTime)
            ).setText("Next $lineNumber in: ^1").build(),
            contentDescription = PlainComplicationText.Builder(text = "Long Text version of Number.")
                .build()
        )
            //.setTapAction(complicationPendingIntent)
            .build()

        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = TimeDifferenceComplicationText.Builder(
                TimeDifferenceStyle.SHORT_DUAL_UNIT,
                CountDownTimeReference(departureTime)
            ).setText("$lineNumber: ^1").build(),
            contentDescription = PlainComplicationText.Builder(text = "Short Text version of Number.")
                .build()
        )
            //.setTapAction(complicationPendingIntent)
            .build()

        else -> {
            Timber.w("Unexpected complication type ${complicationRequest.complicationType}")
            ShortTextComplicationData.Builder(
                text = TimeDifferenceComplicationText.Builder(
                    TimeDifferenceStyle.SHORT_DUAL_UNIT,
                    CountDownTimeReference(departureTime)
                ).setText("U $lineNumber: ^1").build(),
                contentDescription = PlainComplicationText.Builder(text = "Short Text version of Number.")
                    .build()
            )
                //.setTapAction(complicationPendingIntent)
                .build()
        }
    }

private fun buildPlainTextBussiniComplication(
    text: String,
    complicationRequest: ComplicationRequest
) =
    when (complicationRequest.complicationType) {
        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(text).build(),
        ).build()
        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(text).build(),
        ).build()
        else -> null
    }

fun buildErrorBussiniComplication(complicationRequest: ComplicationRequest) =
    buildPlainTextBussiniComplication(
        "Failed to refresh",
        complicationRequest
    )

fun buildNoRouteBussiniComplication(complicationRequest: ComplicationRequest) =
    buildPlainTextBussiniComplication("No route selected", complicationRequest)

fun requestComplicationUpdate(context: Context) {
    val updateRequester = ComplicationDataSourceUpdateRequester.create(
        context,
        ComponentName(context, LegacyBussiniComplicationDataSource::class.java)
    )

    // we dont know the complication ID outside data source
    // update any complication using LegacyBussiniComplicationDataSource
    updateRequester.requestUpdateAll()
}