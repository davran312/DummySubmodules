package namba.wallet.nambaone.common.ui.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

private const val VISIBLE_THRESHOLD = 10

class EndlessScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val loadNextPage: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var isLoading = false
    private var total = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        if (totalItemCount == visibleItemCount) return
        if (isLoading && totalItemCount > total) {
            isLoading = false
            total = totalItemCount
        }
        if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
            loadNextPage()
            isLoading = true
        }
    }
}
