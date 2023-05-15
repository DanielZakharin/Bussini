package fi.danielz.bussini.presentation.directionselection

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
import fi.danielz.bussini.R
import fi.danielz.bussini.preferences.PreferencesManager
import fi.danielz.bussini.preferences.writePattern
import fi.danielz.bussini.presentation.directionselection.compose.DirectionSelectionClickHandler
import fi.danielz.bussini.presentation.directionselection.compose.DirectionSelectionScreen
import fi.danielz.bussini.presentation.routeselection.RouteSelectionViewModel
import javax.inject.Inject

/**
 * Simple fragment for selecting a direction of a route
 * Saves route and direction IDS to preferences
 */
@AndroidEntryPoint
class DirectionSelectionFragment : Fragment() {

    @Inject
    lateinit var prefs: PreferencesManager

    private val navargs by navArgs<DirectionSelectionFragmentArgs>()

    private val vm: RouteSelectionViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    private val clickHandler = object : DirectionSelectionClickHandler {
        override fun onDirectionSelected(routeId: String, directionId: Int) {
            val routePattern = "$routeId:$directionId:01"

            prefs.writePattern(routePattern)

            findNavController().navigate(
                DirectionSelectionFragmentDirections.toStopSelection(
                    routeId,
                    directionId
                )
            )
        }

        override fun onBackPressed() {
            findNavController().popBackStack()
        }

        override fun onReloadClick() {
            vm.reloadRoutes()
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
                val uiState = vm.routeSelectionUIState.collectAsState()
                DirectionSelectionScreen(
                    selectedRouteId = navargs.selectedRouteId,
                    uiState = uiState.value,
                    clickHandler = clickHandler
                )
            }
        }
    }
}