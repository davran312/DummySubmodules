package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.FloatRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.widget_expand_header_view.view.*
import namba.wallet.nambaone.uikit.R

class ExpandHeaderView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var isExpandable: Boolean = true
        set(value) {
            field = value
            arrowIconView.isVisible = value
        }

    init {
        View.inflate(context, R.layout.widget_expand_header_view, this)
        if (attrs != null) {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.ExpandHeaderView)
            val title = attr.getString(R.styleable.ExpandHeaderView_title)
            textView.text = title
            attr.recycle()
        }
    }

    fun setTitle(title: String) {
        textView.text = title
    }

    fun setArrowRotation(@FloatRange(from = .0, to = 1.0) value: Float) {
        val rotation = 1 - value
        arrowIconView.rotation = rotation * -180
    }
}