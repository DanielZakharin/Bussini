package fi.danielz.hslbussin

import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureQueryData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

// access intenal function instead of constructing object and then calling function
// this is due to not being able to access classes in build/generated, which are needed
// to init StopSingleDepartureQueryData class
private fun makeTimeString(minutes: Int, hours: Int): String {
    val currentTime = System.currentTimeMillis()
    val departureTime = currentTime + (minutes * 60000) + (hours * 60 * 60000)
    val interval = departureTime - currentTime
    return StopSingleDepartureQueryData.millisToHoursMinutes(interval)
}

@OptIn(ExperimentalCoroutinesApi::class)
class StopSingleDepartureQueryDataTest {
    @Test
    fun minutesOnlyTest() = runTest {
        listOf(0, 1, 10, 30, 59).forEach {
            val mockItem = makeTimeString(minutes = it, hours = 0)
            assertEquals("Testing with $it minutes:", "${it}min", mockItem)
        }
    }

    @Test
    fun hoursAndMinutesTest() = runTest {
        listOf(0, 1, 10, 30, 59).forEach { minutes ->
            listOf(1, 5, 12, 24).forEach { hours ->
                val mockItem = makeTimeString(minutes, hours)
                assertEquals(
                    "Testing with $minutes minutes & $hours hours:",
                    "${hours}h ${minutes}min",
                    mockItem
                )
            }
        }
    }
}