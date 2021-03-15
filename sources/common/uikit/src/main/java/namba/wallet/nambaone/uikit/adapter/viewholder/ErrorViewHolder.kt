package namba.wallet.nambaone.uikit.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_paging_error.view.error
import kotlinx.android.synthetic.main.item_paging_error.view.retry
import namba.wallet.nambaone.common.utils.extensions.setThrottleOnClickListener
import namba.wallet.nambaone.uikit.R

class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_paging_error, parent, false)
    )

    private val errorTextView = view.error
    private val retryButton = view.retry

    fun onBind(errorText: String, onRetry: () -> Unit) {
        errorTextView.text = errorText
        retryButton.setThrottleOnClickListener { onRetry() }
    }

    fun onBind(@StringRes errorTextRes: Int, onRetry: () -> Unit) {
        errorTextView.setText(errorTextRes)
        retryButton.setThrottleOnClickListener { onRetry() }
    }
}
