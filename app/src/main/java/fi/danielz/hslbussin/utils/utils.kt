package fi.danielz.hslbussin.utils

import android.content.Context
import java.time.Duration

/**
 * Pass to .getSharedPrefrences(name: ...) methods to always retrieve the same instance of shared prefs
 */
const val SHARED_PREFS_NAME = "BUSSINI_SHARED_PREFS"

fun Context.getSharedPrefs() = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

fun millisToHoursMinutes(millis: Long): String {
    val duration = Duration.ofMillis(millis)
    val hoursPart = duration.toHours()
    val minutesPart = duration.minusHours(hoursPart).toMinutes().toInt()
    return if (hoursPart > 0L) {
        "${hoursPart}h ${minutesPart}min"
    } else {
        if (minutesPart <= 0) "< 1min" else
            "${minutesPart}min"
    }
}