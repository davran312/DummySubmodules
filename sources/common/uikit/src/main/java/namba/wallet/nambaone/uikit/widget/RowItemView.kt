package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.item_row_view.view.*
import namba.wallet.nambaone.uikit.R

class RowItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.item_row_view, this)
    }
    fun showItem(title: String, iconUrl: String) {
        rowItemTopTextView.text = title
        rowItemImageView.loadImage(iconUrl)
    }

    fun showItem(title: String, subtitle: String, @DrawableRes iconResId: Int, tint: Int = 0) {
        rowItemTopTextView.text = title
        rowItemBottomTextView.text = subtitle
        rowItemImageView.setPlaceholder(iconResId, tint)
        rowItemBottomTextView.isVisible = true
    }
}