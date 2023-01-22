package fi.danielz.hslbussin.presentation.stopdisplay

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.databinding.FragmentStopDisplayBinding
import fi.danielz.hslbussin.databinding.StopDisplayRowBinding
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.clearSavedPrefs
import fi.danielz.hslbussin.preferences.readStopAndPattern
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopSingleDepartureData
import fi.danielz.hslbussin.utils.recyclerview.SimpleDataBindingRecyclerAdapter
import fi.danielz.hslbussin.utils.recyclerview.SimpleDataBindingRecyclerItem
import fi.danielz.hslbussin.utils.view.setSingleClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// FIXME move to a separate file
abstract class DataBindingFragment<T : ViewDataBinding>(@LayoutRes private val layoutRes: Int) :
    Fragment() {
    lateinit var dataBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, layoutRes, null, false)
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()
        dataBinding.lifecycleOwner = this
    }

    override fun onPause() {
        super.onPause()
        dataBinding.lifecycleOwner = null
    }
}

/**
 * A [Fragment] to display departures from a stop saved in preferences.
 * 'Classic' style of layout, with databinding and XML
 */
@AndroidEntryPoint
class StopDisplayFragment :
    DataBindingFragment<FragmentStopDisplayBinding>(R.layout.fragment_stop_display) {

    private val vm: StopDisplayViewModel by viewModels()

    private val recyclerAdapter = SimpleDataBindingRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs =
            SharedPreferencesManager(requireActivity().getPreferences(Context.MODE_PRIVATE))
        val (stopId, patternId) = prefs.readStopAndPattern()

        vm.init(requireNotNull(stopId), requireNotNull(patternId))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            dataBinding.vm = vm
            dataBinding.departuresRecycler.adapter = recyclerAdapter
            dataBinding.buttonSwitch.setSingleClickListener {
                SharedPreferencesManager(requireActivity().getPreferences(Context.MODE_PRIVATE)).clearSavedPrefs()
                // clear saved prefs and navigate to start of flow
                findNavController()
                    .navigate(StopDisplayFragmentDirections.actionStopDisplayFragmentToRouteSelectionFragment())
            }
            viewLifecycleOwner.lifecycleScope.launch {
                vm.subsequentDepartures.collect {
                    it.takeIf { it.isNotEmpty() }?.let { items ->
                        recyclerAdapter.submitList(mapRecyclerItems(items))
                    }
                }
            }
        }
    }

    private fun mapRecyclerItems(dataItems: List<StopSingleDepartureData>) =
        dataItems.map { departureItem ->
            SimpleDataBindingRecyclerItem<StopSingleDepartureData, StopDisplayRowBinding>(
                departureItem,
                R.layout.stop_display_row
            ) { data, binding ->
                binding.departureItem = data
            }
        }
}