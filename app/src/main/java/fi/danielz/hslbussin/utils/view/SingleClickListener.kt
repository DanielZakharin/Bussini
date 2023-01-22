package fi.danielz.hslbussin.utils.view

import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Click listener with debounce
 * Prevents duplicate button presses in a short window of time
 */
class SingleClickListener(private val doOnClick: (View) -> Unit) : View.OnClickListener {
    private val debounceMillis = 500L
    private val enabled = AtomicBoolean(true)
    override fun onClick(v: View) {
        if (enabled.get()) {
            doOnClick(v)
            v.isEnabled = false
            v.postDelayed({
                v.isEnabled = true
                enabled.set(true)
            }, debounceMillis)
        }
    }
}

fun View.setSingleClickListener(onClick: (View) -> Unit) {
    setOnClickListener(SingleClickListener(onClick))
}