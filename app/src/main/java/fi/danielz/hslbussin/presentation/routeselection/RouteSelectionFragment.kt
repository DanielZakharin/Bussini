/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package fi.danielz.hslbussin.presentation.routeselection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.hasRequiredStopData
import fi.danielz.hslbussin.presentation.routeselection.compose.RouteSelectionScreen

@AndroidEntryPoint
class RouteSelectionFragment : Fragment() {
    private val vm: RouteSelectionViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    override fun onResume() {
        super.onResume()
        // check if a selection exists
        if (SharedPreferencesManager(requireActivity().getPreferences(Context.MODE_PRIVATE)).hasRequiredStopData()) {
            findNavController().navigate(
                RouteSelectionFragmentDirections.actionRouteSelectionFragmentToStopDisplayFragment()
            )
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val routesState = vm.routes.collectAsState(initial = emptyList())
                val errorsState = vm.errors.collectAsState(initial = null)
                RouteSelectionScreen(
                    routesState,
                    errorsState
                ) {
                    this@RouteSelectionFragment.findNavController().navigate(
                        RouteSelectionFragmentDirections.toDirectionSelection(
                            it.gtfsId
                        )
                    )
                }
            }
        }
    }
}
