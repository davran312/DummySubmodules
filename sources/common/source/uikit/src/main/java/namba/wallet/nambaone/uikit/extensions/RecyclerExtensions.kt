package namba.wallet.nambaone.uikit.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class SpaceDecorator(
    private val recyclerView: RecyclerView,
    private var horizontalSpace: Int = 0,
    private var verticalSpace: Int = 0,
    private var bottomSpace: Int = 0
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager

        if (layoutManager is GridLayoutManager) {
            if (layoutManager.orientation == GridLayoutManager.HORIZONTAL) {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                    outRect.right = horizontalSpace
                } else {
                    outRect.right = 0
                }
            } else {
                outRect.right = horizontalSpace
            }
        } else if (layoutManager is LinearLayoutManager) {
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                outRect.right = horizontalSpace
            } else {
                outRect.right = 0
            }
        } else {
            outRect.right = horizontalSpace
        }
        outRect.left = horizontalSpace
        outRect.top = verticalSpace
        outRect.bottom = max(verticalSpace,bottomSpace)
    }
}

fun RecyclerView.addSpaceDecorator(verticalSpace: Int = 0, horizontalSpace: Int = 0, bottomSpace: Int = 0) {
    addItemDecoration(
        SpaceDecorator(
            recyclerView = this,
            horizontalSpace = horizontalSpace,
            verticalSpace = verticalSpace,
            bottomSpace = bottomSpace
        )
    )
}
