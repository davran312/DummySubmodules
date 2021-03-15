package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.widget_tab_radiogroup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import namba.wallet.nambaone.common.utils.extensions.dip
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.colorFromTheme

private const val ANIMATION_DURATION = 200L

class TabsRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {

    var onTabChanged: ((Int) -> Unit)? = null

    val uiScope = CoroutineScope(Dispatchers.Main)
    var switchAnimationDuration: Long = ANIMATION_DURATION

    private val buttons = mutableListOf<RadioButton>()

    @ColorInt
    private val tabTextColor = colorFromTheme(R.attr.colorHighEmphasisOnPrimary)

    init {
        inflate(context, R.layout.widget_tab_radiogroup, this)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            onTabChanged?.invoke(checkedId)
            animateSelector(checkedId)
        }
    }

    fun setTabs(@StringRes vararg titleResourceId: Int, activeResourceId: Int) {
        if (radioGroup.childCount != 0) return

        for (title in titleResourceId) {
            val button = RadioButton(context).apply {
                id = title
                layoutParams = RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, dip(36), 1f)
                text = context.getString(title)
                buttonDrawable = null
                background = null
                gravity = Gravity.CENTER
                isChecked = title == activeResourceId
                setButtonTextAppearance(context, R.style.AppTheme_TextAppearance_Regular12)
                setTextColor(tabTextColor)

            }
            radioGroup.addView(button)
            buttons.add(button)
        }
        uiScope.launch { setupSelectorParams() }
    }

    private fun RadioButton.setButtonTextAppearance(context: Context, @StyleRes resId: Int) {
        this.setTextAppearance(resId)
    }

    private fun setupSelectorParams() {
        if (buttons.isEmpty()) return

        val button = buttons.find { it.isChecked } ?: return
        selector.layoutParams = LayoutParams(button.measuredWidth, button.measuredHeight)
        post { selector.x = button.x }
        selector.invalidate()
    }

    private fun animateSelector(checkedId: Int) {
        val button = buttons.find { it.id == checkedId } ?: return

        uiScope.launch {
            selector.animate()
                .setDuration(switchAnimationDuration)
                .translationX(button.x)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        buttons.forEach { it.isEnabled = enabled }
        super.setEnabled(enabled)
    }
}
