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
import androidx.navigation.fragment.findNavController
import fi.danielz.hslbussin.R
import fi.danielz.hslbussin.databinding.FragmentStopDisplayBinding
import fi.danielz.hslbussin.preferences.SharedPreferencesManager
import fi.danielz.hslbussin.preferences.readStopAndPattern
import timber.log.Timber

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
 * 'Classic' style of layout, with databinding an XML
 */
class StopDisplayFragment :
    DataBindingFragment<FragmentStopDisplayBinding>(R.layout.fragment_stop_display) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            val prefs =
                SharedPreferencesManager(requireActivity().getPreferences(Context.MODE_PRIVATE))
            val (stopId, patternId) = prefs.readStopAndPattern()
            Timber.d("$stopId $patternId")
        }
    }
}