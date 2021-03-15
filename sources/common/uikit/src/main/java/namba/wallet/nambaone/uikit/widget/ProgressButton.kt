package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import kotlinx.android.synthetic.main.widget_progress_button.view.progressBar
import kotlinx.android.synthetic.main.widget_progress_button.view.textView
import namba.wallet.nambaone.uikit.R

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.widget_progress_button, this)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton)

            val progressColor = typedArray.getColor(R.styleable.ProgressButton_progressColor, 0)
            if (progressColor != 0) {
                progressBar.indeterminateDrawable.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(progressColor, BlendModeCompat.DST_ATOP))
            }

            val textAppearance = typedArray.getResourceId(R.styleable.ProgressButton_android_textAppearance, 0)
            if (progressColor != 0) {
                TextViewCompat.setTextAppearance(textView, textAppearance)
            }

            val buttonText = typedArray.getText(R.styleable.ProgressButton_buttonText)
            textView.text = buttonText

            val textStyle = typedArray.getInt(R.styleable.ProgressButton_android_textStyle, Typeface.NORMAL)
            textView.setTypeface(textView.typeface, textStyle)

            val textColor = typedArray.getColor(R.styleable.ProgressButton_android_textColor, 0)
            if (textColor != 0) {
                textView.setTextColor(textColor)
            }

            val imageResId = typedArray.getResourceId(R.styleable.ProgressButton_buttonImage, 0)
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, imageResId, 0)

            val textAllCaps = typedArray.getBoolean(R.styleable.ProgressButton_buttonTextAllCaps, false)
            textView.isAllCaps = textAllCaps

            val isEnabled = typedArray.getBoolean(R.styleable.ProgressButton_enabled, true)
            setEnabled(isEnabled)

            typedArray.recycle()
        }
    }

    fun setLoading(isLoading: Boolean) {
        isEnabled = !isLoading
        textView.isVisible = !isLoading
        progressBar.isVisible = isLoading
    }

    fun setButtonText(@StringRes resId: Int) {
        textView.setText(resId)
    }

    fun setButtonText(text: String) {
        textView.text = text
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        textView.isEnabled = enabled
    }
}
