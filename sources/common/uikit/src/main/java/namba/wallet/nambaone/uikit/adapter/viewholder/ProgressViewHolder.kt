package namba.wallet.nambaone.uikit.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namba.wallet.nambaone.uikit.R

class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_paging_progress, parent, false)
    )
}
