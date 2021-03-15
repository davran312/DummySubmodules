package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_simple_error_view.view.messageTextView
import kotlinx.android.synthetic.main.widget_simple_error_view.view.retryButton
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.colorFromTheme

class SimpleErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : LinearLayout(context, attrs, defStyleAttrs) {

    var onRefreshClickListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.widget_simple_error_view, this)

        orientation = VERTICAL
        gravity = CENTER

        retryButton.setOnClickListener { onRefreshClickListener?.invoke() }
        setBackgroundColor(colorFromTheme(R.attr.colorAlertBackground))

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleErrorView)

            val text = typedArray.getString(R.styleable.SimpleErrorView_text)
            messageTextView.text = text

            typedArray.recycle()
        }
    }
}
