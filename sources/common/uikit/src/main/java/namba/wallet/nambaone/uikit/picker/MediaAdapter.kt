package namba.wallet.nambaone.uikit.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_recent.view.itemPositionTextView
import kotlinx.android.synthetic.main.item_recent.view.mediaThumbnailImageView
import kotlinx.android.synthetic.main.item_recent.view.selectItemView
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.PickerOptions

private const val KEY_SELECTED_POSITIONS = "key_selected_positions"

internal class MediaAdapter(
    @LayoutRes
    private val itemLayoutRes: Int,
    private val pickerOptions: PickerOptions,
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val isMultiSelect = pickerOptions.maxFiles > 1
    private val items = mutableListOf<AdapterMediaItem>()
    private val selectedPositions = mutableSetOf<Int>()

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long = items[position].mediaItem.itemId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MediaItemViewHolder(itemLayoutRes, parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MediaItemViewHolder).bind(
            item = items[position],
            onItemClickListener = { onItemClicked(it) },
            onItemSelectClickListener = { onItemSelected(it, position) }
        )
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        (holder as MediaItemViewHolder).onViewRecycled()
    }

    private fun onItemClicked(item: AdapterMediaItem) {
        if (isMultiSelect) {
            callback.onMediaPreview(
                selectedItems = getSelectedItems(),
                previewItem = item.mediaItem
            )
        } else {
            callback.onMediaPreview(
                selectedItems = emptyList(),
                previewItem = item.mediaItem
            )
        }
    }

    private fun onItemSelected(item: AdapterMediaItem, position: Int) {
        if (isSelected(position)) {
            item.position = -1
            selectedPositions.remove(position)
            updateNumeration()
        } else {
            val selectedItemCount = selectedPositions.size
            if (selectedItemCount < pickerOptions.maxFiles) {
                item.position = selectedItemCount + 1
                selectedPositions.add(position)
            } else {
                callback.onMaxItemsSelected()
            }
        }

        notifyItemChanged(position)
        callback.onMediaListUpdated(selectedPositions.size)
    }

    private fun isSelected(position: Int): Boolean = selectedPositions.contains(position)

    private fun updateNumeration() {
        items.forEach { it.position = -1 }
        selectedPositions.forEachIndexed { index, position ->
            items[position].position = index + 1
            notifyItemChanged(position)
        }
    }

    fun onSaveInstanceState(out: Bundle) {
        out.putIntegerArrayList(KEY_SELECTED_POSITIONS, ArrayList(selectedPositions))
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val selectedItems = savedInstanceState?.getIntegerArrayList(KEY_SELECTED_POSITIONS)
        if (selectedItems != null) selectedPositions.addAll(selectedItems)
    }

    fun setItems(newItems: List<Image>) {
        selectedPositions.clear()
        items.clear()
        items.addAll(newItems.map { AdapterMediaItem(it, isMultiSelect) })
        notifyDataSetChanged()
    }

    fun setSelectedItems(selectedItems: List<Image>) {
        selectedPositions.clear()
        selectedItems.forEach { item ->
            selectedPositions.add(items.indexOfFirst { it.mediaItem.itemId == item.itemId })
        }
        updateNumeration()
        notifyDataSetChanged()
        callback.onMediaListUpdated(selectedPositions.size)
    }

    fun getSelectedItems(): List<Image> =
        selectedPositions
            .map { position -> items[position] }
            .sortedBy { it.position }
            .map { it.mediaItem }

    internal interface Callback {

        fun onMediaListUpdated(selectedItemsCount: Int)

        fun onMediaPreview(selectedItems: List<Image>, previewItem: Image? = null)

        fun onMaxItemsSelected()
    }
}

private data class AdapterMediaItem(
    val mediaItem: Image,
    val isMultiSelect: Boolean,
    var position: Int = -1
) {
    val isSelected: Boolean
        get() = position != -1
}

private class MediaItemViewHolder(@LayoutRes layoutRes: Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)) {

    private val itemPositionTextView = itemView.itemPositionTextView
    private val mediaThumbnailImageView = itemView.mediaThumbnailImageView
    private val selectItemView = itemView.selectItemView

    fun bind(
        item: AdapterMediaItem,
        onItemClickListener: (AdapterMediaItem) -> Unit,
        onItemSelectClickListener: (AdapterMediaItem) -> Unit
    ) {
        val mediaItem = item.mediaItem
        val thumbnailBuilder = Glide.with(mediaThumbnailImageView)
            .load(mediaItem.thumbnail?.uri)
            .transform(FitCenter(), CenterCrop())
            .transition(DrawableTransitionOptions.withCrossFade())

        Glide.with(mediaThumbnailImageView)
            .load(mediaItem.uri)
            .thumbnail(thumbnailBuilder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(FitCenter(), CenterCrop())
            .into(mediaThumbnailImageView)

        itemPositionTextView.isVisible = item.isMultiSelect
        selectItemView.isVisible = item.isMultiSelect
        if (item.isMultiSelect) {
            itemView.isActivated = item.isSelected
            itemPositionTextView.isActivated = item.isSelected
            if (item.position > 0) {
                itemPositionTextView.text = item.position.toString()
            } else {
                itemPositionTextView.text = null
            }
            selectItemView.setOnClickListener { onItemSelectClickListener(item) }
        }

        itemView.setOnClickListener { onItemClickListener(item) }
    }

    fun onViewRecycled() {
        Glide.with(itemView.context.applicationContext).clear(mediaThumbnailImageView)
        mediaThumbnailImageView.setImageDrawable(null)
    }
}
