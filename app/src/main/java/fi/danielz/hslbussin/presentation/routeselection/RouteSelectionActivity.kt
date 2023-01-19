/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package fi.danielz.hslbussin.presentation.routeselection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreen

@AndroidEntryPoint
class RouteSelectionActivity : ComponentActivity() {

    private val vm: RouteSelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RouteSelectionScreen()
        }
    }
}
