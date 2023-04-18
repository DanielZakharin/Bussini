package fi.danielz.hslbussin.preferences


/**
 * Convenience function to write route name to prefs
 */
fun SharedPreferencesManager.writeRoute(routeName: String) {
    writeString(SELECTED_ROUTE_ID_KEY, routeName)
}

/**
 * Convenience function to write route pattern to prefs
 */
fun SharedPreferencesManager.writePattern(patternId: String) {
    writeString(SELECTED_PATTERN_GTFSID_KEY, patternId)
}

/**
 * Convenience function to write pattern to prefs
 */
fun SharedPreferencesManager.writeStop(stopGtfsId: String) {
    writeString(SELECTED_STOP_GTFSID_KEY, stopGtfsId)
}

/**
 * Convenience function to read both stop and direction at once
 */
fun SharedPreferencesManager.readStopAndPattern(): Pair<String?, String?> {
    return readString(SELECTED_STOP_GTFSID_KEY) to readString(SELECTED_PATTERN_GTFSID_KEY)
}

fun SharedPreferencesManager.readRouteName(): String = readString(SELECTED_ROUTE_ID_KEY) ?: ""

/**
 * Convenience function to check if a route + stop has previously been selected
 */

fun SharedPreferencesManager.hasRequiredStopData(): Boolean {
    return sharedPrefs.contains(SELECTED_STOP_GTFSID_KEY) && sharedPrefs.contains(
        SELECTED_PATTERN_GTFSID_KEY
    )
}

fun SharedPreferencesManager.clearSavedPrefs() {
    sharedPrefs.edit().remove(SELECTED_PATTERN_GTFSID_KEY).apply()
    sharedPrefs.edit().remove(SELECTED_STOP_GTFSID_KEY).apply()
}