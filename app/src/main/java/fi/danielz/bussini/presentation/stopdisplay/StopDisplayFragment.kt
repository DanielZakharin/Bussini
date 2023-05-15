package fi.danielz.bussini.presentation.stopdisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.bussini.complication.requestComplicationUpdate
import fi.danielz.bussini.preferences.PreferencesManager
import fi.danielz.bussini.preferences.clearSavedPrefs
import fi.danielz.bussini.preferences.readRouteName
import fi.danielz.bussini.preferences.readStopAndPattern
import fi.danielz.bussini.presentation.stopdisplay.compose.StopDisplayClickHandler
import fi.danielz.bussini.presentation.stopdisplay.compose.StopDisplayScreen
import fi.danielz.bussini.presentation.stopdisplay.compose.StopDisplayScreenUIState
import javax.inject.Inject

/**
 * A [Fragment] to display departures from a stop saved in preferences.
 * 'Classic' style of layout, with databinding and XML
 */
@AndroidEntryPoint
class StopDisplayFragment : Fragment() {

    private val vm: StopDisplayViewModel by viewModels()

    @Inject
    lateinit var prefs: PreferencesManager

    private val clickHandler = object : StopDisplayClickHandler {
        override fun onSwitchRoutePressed() {
            prefs.clearSavedPrefs()
            // Show 'blank' complication, since we dont know what to show anymore
            requestComplicationUpdate(requireContext())
            findNavController()
                .navigate(StopDisplayFragmentDirections.actionStopDisplayFragmentToRouteSelectionFragment())
        }

        override fun onRetryPressed() {
            vm.reload()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val (stopId, patternId) = prefs.readStopAndPattern()

        vm.init(requireNotNull(stopId), requireNotNull(patternId), prefs.readRouteName())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext(), null, 0).apply {
            setContent {
                val uiState =
                    vm.uiState.collectAsState(initial = StopDisplayScreenUIState.Loading())
                val ticker = vm.tickerFlow.collectAsState(initial = System.currentTimeMillis())
                StopDisplayScreen(uiState = uiState.value, ticker = ticker, clickHandler)
            }
        }
    }
}