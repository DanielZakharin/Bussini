package fi.danielz.hslbussin.presentation.stopselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.complication.requestComplicationUpdate
import fi.danielz.hslbussin.preferences.PreferencesManager
import fi.danielz.hslbussin.preferences.writeStop
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionClickHandler
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionScreen
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionScreenUIState
import javax.inject.Inject

/**
 * Fragment for displaying route stops
 * Writes stop gtfsId to preferences
 */
@AndroidEntryPoint
class StopSelectionFragment : Fragment() {
    private val vm: StopSelectionViewModel by viewModels()

    private val args by navArgs<StopSelectionFragmentArgs>()

    @Inject
    lateinit var prefs: PreferencesManager

    private val clickHandler = object : StopSelectionClickHandler {
        override fun onStopSelectedClick(stopGtfsId: String) {
            prefs.writeStop(stopGtfsId)
            // start showing complication as soon as we know what to show
            requestComplicationUpdate(requireContext())
            findNavController().navigate(
                StopSelectionFragmentDirections.actionStopSelectionFragmentToStopDisplayFragment()
            )
        }

        override fun onReloadClick() {
            vm.reload()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 'init' the vm
        vm.setPatternGtfsId("${args.routeId}:${args.directionId}:01")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState =
                    vm.uiState.collectAsState(initial = StopSelectionScreenUIState.Loading())
                StopSelectionScreen(
                    uiState = uiState.value,
                    clickHandler = clickHandler
                )
            }
        }
    }
}