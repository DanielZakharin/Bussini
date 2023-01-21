package fi.danielz.hslbussin.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import timber.log.Timber

abstract class SimpleRecyclerItem(protected open val item: Any) {
    abstract fun inflateView(layoutInflater: LayoutInflater): View
    abstract val itemViewType: Int
    abstract fun onBind(holder: ViewHolder)
}


class SimpleDataBindingRecyclerItem<T : Any, B : ViewDataBinding>(
    override val item: T,
    @LayoutRes private val layoutRes: Int,
    private val onBind: (T, B) -> Unit
) :
    SimpleRecyclerItem(item) {
    fun inflateBinding(layoutInflater: LayoutInflater): B =
        DataBindingUtil.inflate(layoutInflater, layoutRes, null, false)

    override fun inflateView(layoutInflater: LayoutInflater): View {
        return inflateBinding(layoutInflater).root
    }

    override val itemViewType: Int = layoutRes

    override fun onBind(holder: ViewHolder) {
        // ensure binding
        val binding: B? = DataBindingUtil.getBinding(holder.itemView)
            ?: DataBindingUtil.bind(holder.itemView) as? B
        if (binding == null) {
            Timber.w("Could not find or bind a databinding for item!")
            return
        }
        onBind(item, binding)
    }
}

/** Simple RecyclerView Adapter that accepts a list of [SimpleRecyclerItem]'s
 * @param items items to create rows from. Layouts used by item must be databinding layouts **/
class SimpleDataBindingRecyclerAdapter<T>(
    private val items: List<SimpleRecyclerItem>
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, true)
        return object : ViewHolder(view) {}

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].onBind(holder)
    }

    override fun getItemViewType(position: Int): Int = items[position].itemViewType
}