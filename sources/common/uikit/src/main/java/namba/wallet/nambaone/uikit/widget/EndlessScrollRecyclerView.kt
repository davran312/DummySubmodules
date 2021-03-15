package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.widget_endless_scroll_recyclerview.view.*
import namba.wallet.nambaone.common.ui.recyclerview.EndlessScrollListener
import namba.wallet.nambaone.common.ui.recyclerview.ListState
import namba.wallet.nambaone.common.utils.extensions.attachAdapter
import namba.wallet.nambaone.uikit.R

class EndlessScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {
    var emptyView: View? = null
        private set
    var errorView: View? = null
        private set
    private var progressView: View? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

    init {
        View.inflate(context, R.layout.widget_endless_scroll_recyclerview, this)
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.EndlessScrollRecyclerView)
        if (ta.hasValue(R.styleable.EndlessScrollRecyclerView_emptyLayout)) {
            val emptyLayoutId =
                ta.getResourceId(R.styleable.EndlessScrollRecyclerView_emptyLayout, 0)
            emptyView = LayoutInflater.from(context).inflate(emptyLayoutId, this, false)
            addView(emptyView)
        }
        if (ta.hasValue(R.styleable.EndlessScrollRecyclerView_errorLayout)) {
            val errorLayoutId =
                ta.getResourceId(R.styleable.EndlessScrollRecyclerView_errorLayout, 0)
            errorView = LayoutInflater.from(context).inflate(errorLayoutId, this, false)
            addView(errorView)
        }
        if (ta.hasValue(R.styleable.EndlessScrollRecyclerView_progressLayout)) {
            val shimmerLayoutId =
                ta.getResourceId(R.styleable.EndlessScrollRecyclerView_progressLayout, 0)
            progressView = LayoutInflater.from(context).inflate(shimmerLayoutId, this, false)
            addView(progressView)
        }
        val isNestedScrollingEnabled = ta.getBoolean(
            R.styleable.EndlessScrollRecyclerView_android_nestedScrollingEnabled,
            false
        )
        recyclerView.isNestedScrollingEnabled = isNestedScrollingEnabled
        ta.recycle()
        emptyView?.isVisible = false
        errorView?.isVisible = false
        progressView?.isVisible = false
        setState(ListState.Initial)
    }

    fun enablePaging(onLoadMoreLister: (() -> Unit)) {
        if (layoutManager !is LinearLayoutManager) return
        val scrollListener =
            EndlessScrollListener(layoutManager as LinearLayoutManager) {
                onLoadMoreLister()
            }
        recyclerView.addOnScrollListener(scrollListener)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
        recyclerView.attachAdapter(this.adapter ?: return)
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    recyclerView.post { recyclerView?.smoothScrollToPosition(0) }
                }
            }
        }
        doOnAttach { adapter.registerAdapterDataObserver(observer) }
        doOnDetach {
            if (adapter.hasObservers()) {
                adapter.unregisterAdapterDataObserver(observer)
            }
        }
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        this.layoutManager = layoutManager
        recyclerView.layoutManager = layoutManager
    }

    fun addItemDecoration(decoration: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(decoration)
    }

    fun removeItemDecoration(decoration: RecyclerView.ItemDecoration) {
        recyclerView.removeItemDecoration(decoration)
    }

    fun getRecyclerview() = recyclerView

    fun setState(state: ListState) {
        val isDataEmpty: Boolean = this.adapter.isEmpty()
        errorView?.isVisible = state is ListState.Error && isDataEmpty
        recyclerView.isVisible = state !is ListState.Initial && !isDataEmpty
        emptyView?.isVisible = isDataEmpty && state is ListState.Idle
        progressView?.isVisible =
            state is ListState.Initial || state is ListState.Loading && isDataEmpty
    }

    fun setContentPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        recyclerView.updatePadding(left = left, top = top, right = right, bottom = bottom)
        emptyView?.updatePadding(left = left, top = top, right = right, bottom = bottom)
        errorView?.updatePadding(left = left, top = top, right = right, bottom = bottom)
        progressView?.updatePadding(left = left, top = top, right = right, bottom = bottom)
    }
}

private fun RecyclerView.Adapter<*>?.isEmpty() =
    (this?.itemCount == null || this.itemCount == 0)
