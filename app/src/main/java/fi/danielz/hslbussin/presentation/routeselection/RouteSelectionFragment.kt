/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package fi.danielz.hslbussin.presentation.routeselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.preferences.PreferencesManager
import fi.danielz.hslbussin.preferences.hasRequiredStopData
import fi.danielz.hslbussin.preferences.writeRoute
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionClickHandler
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreen
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import javax.inject.Inject

@AndroidEntryPoint
class RouteSelectionFragment : Fragment() {
    private val vm: RouteSelectionViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    private val clickHandler = object : RouteSelectionClickHandler {
        override fun onRouteSelected(data: RouteData) {
            // save route name for display
            prefs.writeRoute(data.shortName)
            this@RouteSelectionFragment.findNavController().navigate(
                RouteSelectionFragmentDirections.toDirectionSelection(
                    data.gtfsId
                )
            )
        }

        override fun onRetryErrorClick() {
            vm.reloadRoutes()
        }
    }

    @Inject
    lateinit var prefs: PreferencesManager

    override fun onResume() {
        super.onResume()
        // check if a selection exists
        if (prefs.hasRequiredStopData()) {
            findNavController().navigate(
                RouteSelectionFragmentDirections.actionRouteSelectionFragmentToStopDisplayFragment()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState = vm.routeSelectionUIState.collectAsStateWithLifecycle()
                RouteSelectionScreen(
                    uiState.value,
                    clickHandler
                )
            }
        }
    }
}
