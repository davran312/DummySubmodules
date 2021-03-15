package namba.wallet.nambaone.uikit.picker

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val insetVertical: Int,
    private val insetHorizontal: Int
) : RecyclerView.ItemDecoration() {

    constructor(insetWidth: Int) : this(insetWidth, insetWidth)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val itemSpanIndex = (view.layoutParams as? GridLayoutManager.LayoutParams)?.spanIndex ?: 0
        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val orientation = (parent.layoutManager as? GridLayoutManager)?.orientation
            ?: (parent.layoutManager as? LinearLayoutManager)?.orientation
            ?: RecyclerView.VERTICAL

        if (orientation == RecyclerView.VERTICAL) {
            outRect.left = if (itemSpanIndex == 0) 0 else insetHorizontal
            outRect.top = if (position < spanCount) 0 else insetVertical
        } else {
            outRect.left = if (position < spanCount) 0 else insetHorizontal
            outRect.top = if (itemSpanIndex == 0) 0 else insetVertical
        }
        outRect.right = 0
        outRect.bottom = 0
    }
}
