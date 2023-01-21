package fi.danielz.hslbussin.presentation.stopselection

import android.content.Context
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
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.writeStop
import fi.danielz.hslbussin.presentation.stopselection.compose.StopSelectionScreen

/**
 * Fragment for displaying route stops
 * Writes stop gtfsId to preferences
 */
@AndroidEntryPoint
class StopSelectionFragment : Fragment() {
    private val vm: StopSelectionViewModel by viewModels()

    private val args by navArgs<StopSelectionFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val patternId = "${args.routeId}:${args.directionId}:01"
                val stopState = vm.stops(patternId).collectAsState(initial = emptyList())
                val errorState = vm.errors.collectAsState(initial = null)
                StopSelectionScreen(stopsState = stopState, errorState = errorState, {
                    findNavController().popBackStack()
                }) { stopId ->
                    SharedPreferencesManager(
                        requireActivity().getPreferences(Context.MODE_PRIVATE)
                    ).writeStop(stopId)
                    findNavController().navigate(
                        StopSelectionFragmentDirections.actionStopSelectionFragmentToStopDisplayFragment()
                    )
                }
            }
        }
    }
}