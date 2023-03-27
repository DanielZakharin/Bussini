package fi.danielz.hslbussin.presentation.directionselection

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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.writePattern
import fi.danielz.hslbussin.presentation.directionselection.compose.DirectionSelectionScreen
import fi.danielz.hslbussin.presentation.routeselection.RouteSelectionViewModel

/**
 * Simple fragment for selecting a direction of a route
 * Saves route and direction IDS to preferences
 */
@AndroidEntryPoint
class DirectionSelectionFragment : Fragment() {

    private val navargs by navArgs<DirectionSelectionFragmentArgs>()

    private val vm: RouteSelectionViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState = vm.routeSelectionUIState.collectAsState()
                DirectionSelectionScreen(navargs.selectedRouteId, uiState.value, {
                    findNavController().popBackStack()
                }) { routeId, directionId ->
                    val routePattern = "$routeId:$directionId:01"

                    SharedPreferencesManager(
                        requireActivity().getPreferences(Context.MODE_PRIVATE)
                    ).writePattern(routePattern)

                    findNavController().navigate(
                        DirectionSelectionFragmentDirections.toStopSelection(
                            routeId,
                            directionId
                        )
                    )
                }
            }
        }
    }
}