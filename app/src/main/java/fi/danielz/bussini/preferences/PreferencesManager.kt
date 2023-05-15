package fi.danielz.bussini.preferences

import android.content.SharedPreferences

const val SELECTED_ROUTE_ID_KEY = "SELECTED_ROUTE_ID_KEY"
const val SELECTED_STOP_GTFSID_KEY = "SELECTED_STOP_GTFSID_KEY"

/**
 * Format HSL:123B:1:01 - route id + direction + static '01'
 */
const val SELECTED_PATTERN_GTFSID_KEY = "SELECTED_PATTERN_GTFSID_KEY"

/**
 * Manager for storin key value pairs related to the app function on disk
 */
interface PreferencesManager {
    fun writeInt(key: String, value: Int)
    fun writeString(key: String, value: String)

    fun readString(key: String): String?

    fun readInt(key: String): Int

    fun hasEntry(key: String): Boolean

    fun removeEntry(key: String)
}

/**
 * Implementation of [PreferencesManager] that stores values into {@link android.app.Activity.getPreferences Activity.getPreferences}
 */
// TODO make this injectable with DI
class SharedPreferencesManager constructor(private val sharedPrefs: SharedPreferences) :
    PreferencesManager {
    override fun writeInt(key: String, value: Int) = with(sharedPrefs.edit()) {
        putInt(key, value)
        apply()
    }

    override fun writeString(key: String, value: String) = with(sharedPrefs.edit()) {
        putString(key, value)
        apply()
    }

    override fun readString(key: String): String? = sharedPrefs.getString(key, "")

    override fun readInt(key: String): Int = sharedPrefs.getInt(key, Int.MIN_VALUE)

    override fun hasEntry(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    override fun removeEntry(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }
}
