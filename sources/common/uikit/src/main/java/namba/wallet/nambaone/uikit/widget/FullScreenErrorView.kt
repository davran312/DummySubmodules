package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.widget_full_screen_error_view.view.messageTextView
import kotlinx.android.synthetic.main.widget_full_screen_error_view.view.refreshButton
import namba.wallet.nambaone.uikit.R

class FullScreenErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {

    var onRefreshClickListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.widget_full_screen_error_view, this)
        refreshButton.setOnClickListener { onRefreshClickListener?.invoke() }

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FullScreenErrorView)

            val text = typedArray.getString(R.styleable.FullScreenErrorView_text)
            messageTextView.text = text

            typedArray.recycle()
        }
    }
}
