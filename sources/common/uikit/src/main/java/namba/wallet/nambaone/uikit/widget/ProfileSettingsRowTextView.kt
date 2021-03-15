package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.widget_profile_setting_row.view.*
import namba.wallet.nambaone.uikit.R

class ProfileSettingsRowTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.widget_profile_setting_row, this)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ProfileSettingsRowTextView)
            val topText = ta.getString(R.styleable.ProfileSettingsRowTextView_topText)
            val bottomText = ta.getString(R.styleable.ProfileSettingsRowTextView_bottomText)
            val drawable = ta.getDrawable(R.styleable.ProfileSettingsRowTextView_icon)
            if (drawable != null) {
                val defaultColor = ContextCompat.getColor(context, R.color.highEmphasisOnPrimary)
                val tint = ta.getColor(R.styleable.ProfileSettingsRowTextView_iconTint, defaultColor)
                avatarImageView.setPlaceholder(drawable, tint)
            }
            topTextView.text = topText
            bottomTextView.text = bottomText
            bottomTextView.isVisible = !bottomText.isNullOrEmpty()
            ta.recycle()
        }
    }

    fun setBottomText(@StringRes resourceId: Int) {
        bottomTextView.setText(resourceId)
    }

    fun setTopText(text: String) {
        topTextView.text = text
    }

    fun setBottomText(text: String) {
        bottomTextView.text = text
        bottomTextView.isVisible = true
    }

    fun setImageResource(@DrawableRes drawableResId: Int, tint: Int) {
        avatarImageView.setPlaceholder(drawableResId, tint)
    }

    fun setImageResource(@DrawableRes drawableResId: Int) {
        avatarImageView.setPlaceholder(drawableResId)
    }

    fun setDropDown(isDropdown: Boolean) {
        arrowImageView.isVisible = isDropdown
    }
}
