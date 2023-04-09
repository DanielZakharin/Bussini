package fi.danielz.hslbussin.presentation.stopdisplay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.readStopAndPattern
import fi.danielz.hslbussin.presentation.stopdisplay.compose.StopDisplayScreen
import fi.danielz.hslbussin.presentation.stopdisplay.compose.StopDisplayScreenUIState
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A [Fragment] to display departures from a stop saved in preferences.
 * 'Classic' style of layout, with databinding and XML
 */
@AndroidEntryPoint
class StopDisplayFragment : Fragment() {

    private val vm: StopDisplayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs =
            SharedPreferencesManager(requireActivity().getPreferences(Context.MODE_PRIVATE))
        val (stopId, patternId) = prefs.readStopAndPattern()

        vm.init(requireNotNull(stopId), requireNotNull(patternId))
        lifecycleScope.launch {
            vm.uiState.collect {
                Timber.d(it.toString())
            }
        }
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
                StopDisplayScreen(uiState = uiState.value, onBackPressed = {

                }) {

                }
            }
        }
    }
}