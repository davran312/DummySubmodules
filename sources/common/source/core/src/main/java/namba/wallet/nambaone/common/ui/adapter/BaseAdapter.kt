package namba.wallet.nambaone.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import namba.wallet.nambaone.common.utils.extensions.setThrottleOnClickListener

abstract class BaseAdapter<T : Any> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    protected var items = mutableListOf<T>()
    private val holderCreators =
        LinkedHashMap<Class<out T>, (parent: ViewGroup) -> BaseViewHolder<out T>>()
    private var viewTypes = SparseArrayCompat<Class<out T>>()

    protected fun <I : T, VH : BaseViewHolder<out T>> addViewHolderCreator(
        itemClass: Class<I>,
        creator: (parent: ViewGroup) -> VH
    ) {
        holderCreators[itemClass] = creator
        viewTypes.put(viewTypes.size(), itemClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val creator = holderCreators[viewTypes[viewType]]
            ?: throw IllegalStateException("Creator not found for this viewType - $viewType")
        return creator.invoke(parent) as BaseViewHolder<T>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.performBind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        viewTypes.indexOfValue(items[position]::class.java)

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: BaseViewHolder<T>) {
        holder.onViewRecycled()
        super.onViewRecycled(holder)
    }

    open fun setData(data: List<T>) {
        if (holderCreators.isEmpty() || holderCreators.size != viewTypes.size()) {
            throw IllegalStateException("Holder creators are not registered, use addViewHolderCreator method")
        }
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = items.isEmpty()

    fun isNotEmpty(): Boolean = items.isNotEmpty()

    fun clear() {
        setData(emptyList())
    }

    fun getItem(position: Int) = items[position]

    fun removeItem(predicate: (T) -> Boolean) {
        val position = items.indexOfFirst(predicate)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}

abstract class BaseViewHolder<T>(
    @LayoutRes layoutId: Int,
    parent: ViewGroup,
    private val onItemClickListener: ((T) -> Unit)? = null
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false)) {

    fun performBind(item: T) {
        onItemClickListener?.let { itemView.setThrottleOnClickListener { it(item) } }
        bind(item)
    }

    protected abstract fun bind(item: T)

    open fun onViewRecycled() = Unit
}
