package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.item_merchant.view.*
import namba.wallet.nambaone.uikit.R

class MerchantView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {
    init {
        View.inflate(context, R.layout.item_merchant, this)
        merchantLayout.clipToOutline = true
    }

    fun setTopText(text: String) {
        userNameTextView.text = text
    }

    fun setBottomText(text: String) {
        categoryTextView.text = text
    }

    fun setIcon(icon: String?, @DrawableRes placeholderDrawable: Int = R.drawable.ic_camera) {
        merchantIcon.setPlaceholder(placeholderDrawable)
        icon?.let { merchantIcon.loadImage(it) }
    }
}
