package fi.danielz.bussini.utils.databinding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("bind:isVisible")
fun setIsVisible(view:View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * For those views that dont like being se to View.GONE
 */
@BindingAdapter("bind:isInvisible")
fun setIsInvisible(view: View, isInvisible: Boolean) {
    view.visibility = if(isInvisible) View.INVISIBLE else View.VISIBLE
}