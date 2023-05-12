package fi.danielz.hslbussin.complication

import androidx.wear.watchface.complications.data.*
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

fun buildErrorBussiniComplication(complicationRequest: ComplicationRequest) =
    when (complicationRequest.complicationType) {
        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("Failed to refresh").build(),
            contentDescription = PlainComplicationText.Builder("Failed to refresh").build(),
        ).build()
        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("Failed to refresh").build(),
            contentDescription = PlainComplicationText.Builder("Failed to refresh").build(),
        ).build()
        else -> null
    }