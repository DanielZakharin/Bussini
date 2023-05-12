package fi.danielz.hslbussin.preferences

import fi.danielz.hslbussin.utils.trimToNull


/**
 * Convenience function to write route name to prefs
 */
fun PreferencesManager.writeRoute(routeName: String) {
    writeString(SELECTED_ROUTE_ID_KEY, routeName)
}

/**
 * Convenience function to write route pattern to prefs
 */
fun PreferencesManager.writePattern(patternId: String) {
    writeString(SELECTED_PATTERN_GTFSID_KEY, patternId)
}

/**
 * Convenience function to write pattern to prefs
 */
fun PreferencesManager.writeStop(stopGtfsId: String) {
    writeString(SELECTED_STOP_GTFSID_KEY, stopGtfsId)
}

/**
 * Convenience function to read both stop and direction at once
 */
fun PreferencesManager.readStopAndPattern(): Pair<String?, String?> {
    return readString(SELECTED_STOP_GTFSID_KEY).trimToNull() to readString(SELECTED_PATTERN_GTFSID_KEY).trimToNull()
}

fun PreferencesManager.readRouteName(): String = readString(SELECTED_ROUTE_ID_KEY) ?: ""

/**
 * Convenience function to check if a route + stop has previously been selected
 */

fun PreferencesManager.hasRequiredStopData(): Boolean {
    return hasEntry(SELECTED_STOP_GTFSID_KEY) && hasEntry(SELECTED_PATTERN_GTFSID_KEY)
}

fun PreferencesManager.clearSavedPrefs() {
    removeEntry(SELECTED_PATTERN_GTFSID_KEY)
    removeEntry(SELECTED_STOP_GTFSID_KEY)
}