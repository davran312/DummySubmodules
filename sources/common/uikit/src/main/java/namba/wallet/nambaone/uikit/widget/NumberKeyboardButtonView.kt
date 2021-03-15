package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.widget_number_button.view.keyboardButtonImageView
import kotlinx.android.synthetic.main.widget_number_button.view.keyboardButtonTextView
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.resFromTheme

class NumberKeyboardButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.widget_number_button, this)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardButtonView)

            val text = typedArray.getString(R.styleable.NumberKeyboardButtonView_keyboard_button_text)
            if (text != null) {
                keyboardButtonTextView.text = text
            }

            val image = typedArray.getDrawable(R.styleable.NumberKeyboardButtonView_keyboard_button_image)
            if (image != null) {
                keyboardButtonImageView.setImageDrawable(image)
                keyboardButtonImageView.visibility = View.VISIBLE
            }

            typedArray.recycle()
        }

        setBackgroundResource(R.drawable.background_keyboard_circle)
        foreground = ContextCompat.getDrawable(context, resFromTheme(android.R.attr.selectableItemBackground))

        clipToOutline = true
    }

    fun setIcon(@DrawableRes drawableResId: Int) {
        keyboardButtonImageView.setImageResource(drawableResId)
        keyboardButtonImageView.visibility = View.VISIBLE
    }

    fun setPressedColor(color: Int) {
        val drawable = context.getDrawable(R.drawable.keyboard_button_pressed) as RippleDrawable
        drawable.setColor(ColorStateList.valueOf(color))
        background = drawable
    }
}
