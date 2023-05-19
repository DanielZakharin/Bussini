package fi.danielz.bussini.complication

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import fi.danielz.bussini.R
import timber.log.Timber
import java.time.Instant

internal fun buildCountdownComplication(
    lineNumber: String,
    departureTime: Instant,
    complicationRequest: ComplicationRequest,
    context: Context
) =
    when (complicationRequest.complicationType) {

        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = TimeDifferenceComplicationText.Builder(
                TimeDifferenceStyle.SHORT_DUAL_UNIT,
                CountDownTimeReference(departureTime)
            ).setDisplayAsNow(false)
                .setText(context.getString(R.string.complication_coundown_long, lineNumber))
                .build(),
            contentDescription = PlainComplicationText.Builder(text = context.getString(R.string.complication_content_description))
                .build()
        ).setMonochromaticImage(
            monochromaticAppIcon()
        )
            .build()

        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = TimeDifferenceComplicationText.Builder(
                TimeDifferenceStyle.SHORT_DUAL_UNIT,
                CountDownTimeReference(departureTime)
            ).setDisplayAsNow(false)
                .setText(context.getString(R.string.complication_coundown_short))
                .build(),
            contentDescription = PlainComplicationText.Builder(text = context.getString(R.string.complication_content_description))
                .build()
        ).setMonochromaticImage(
            monochromaticAppIcon()
        )
            .build()

        else -> {
            Timber.w("Unexpected complication type ${complicationRequest.complicationType}")
            ShortTextComplicationData.Builder(
                text = TimeDifferenceComplicationText.Builder(
                    TimeDifferenceStyle.SHORT_DUAL_UNIT,
                    CountDownTimeReference(departureTime)
                ).setDisplayAsNow(false)
                    .setText(context.getString(R.string.complication_coundown_short))
                    .build(),
                contentDescription = PlainComplicationText.Builder(text = context.getString(R.string.complication_content_description))
                    .build()
            )
                .build()
        }
    }

private fun monochromaticAppIcon(iconRes: Int = R.drawable.ic_bus_default) =
    MonochromaticImage.Builder(
        Icon.createWithResource("fi.danielz.bussini", iconRes)
    ).build()

private fun buildPlainTextBussiniComplication(
    complicationRequest: ComplicationRequest,
    shortText: String,
    longText: String = shortText,
    icon: () -> MonochromaticImage = { monochromaticAppIcon() }
) =
    when (complicationRequest.complicationType) {
        ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(shortText).build(),
            contentDescription = PlainComplicationText.Builder(shortText).build(),
        ).build()
        ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(longText).build(),
            contentDescription = PlainComplicationText.Builder(shortText).build(),
        ).setMonochromaticImage(
            icon()
        ).build()
        else -> {
            Timber.w("Unsupported complication type ${complicationRequest.complicationType.name}")
            null
        }
    }

fun buildErrorBussiniComplication(complicationRequest: ComplicationRequest, context: Context) =
    buildPlainTextBussiniComplication(
        complicationRequest,
        shortText = context.getString(R.string.complication_error_short),
        longText = context.getString(R.string.complication_error_long)
    ) {
        monochromaticAppIcon(R.drawable.ic_bus_error)
    }

fun buildNoRouteBussiniComplication(complicationRequest: ComplicationRequest, context: Context) =
    buildPlainTextBussiniComplication(
        complicationRequest,
        shortText = context.getString(R.string.complication_no_route_short),
        longText = context.getString(R.string.complication_no_route_long),
    ) {
        monochromaticAppIcon(R.drawable.ic_bus_questionmark)
    }

fun buildNoDeparturesBussiniComplication(
    complicationRequest: ComplicationRequest,
    context: Context
) =
    buildPlainTextBussiniComplication(
        complicationRequest,
        shortText = context.getString(R.string.complication_no_departures_short),
        longText = context.getString(R.string.complication_no_departures_long)
    ) {
        monochromaticAppIcon(R.drawable.ic_bus_questionmark)
    }

fun requestComplicationUpdate(context: Context) {
    val updateRequester = ComplicationDataSourceUpdateRequester.create(
        context,
        ComponentName(context, LegacyBussiniComplicationDataSource::class.java)
    )

    // we dont know the complication ID outside data source
    // update any complication using LegacyBussiniComplicationDataSource
    updateRequester.requestUpdateAll()
}