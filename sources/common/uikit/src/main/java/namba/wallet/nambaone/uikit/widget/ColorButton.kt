package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.widget_color_button.view.*
import namba.wallet.nambaone.uikit.R

class ColorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    attrDefStyle: Int = 0
) : ConstraintLayout(context, attrs, attrDefStyle) {
    init {
        View.inflate(context, R.layout.widget_color_button, this)
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.ColorButton)
        val topText = attrs.getString(R.styleable.ColorButton_android_text)
        val bottomText = attrs.getString(R.styleable.ColorButton_android_description)

        topTextView.text = topText
        bottomTextView.text = bottomText
        attrs.recycle()
    }
}