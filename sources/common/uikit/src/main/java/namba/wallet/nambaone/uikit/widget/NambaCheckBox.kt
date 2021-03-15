package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import namba.wallet.nambaone.uikit.R

class NambaCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatCheckBox(context, attrs) {

    private var state = UNCHECKED

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NambaCheckBox)
        state = if (typedArray.getBoolean(R.styleable.NambaCheckBox_indeterminate, false))
            INDETERMINATE else UNCHECKED
        state = if (typedArray.getBoolean(R.styleable.NambaCheckBox_checked, false))
            CHECKED else UNCHECKED
        updateButton()
        setOnCheckedChangeListener { _, _ ->
            state = when (state) {
                INDETERMINATE -> CHECKED
                UNCHECKED -> INDETERMINATE
                CHECKED -> UNCHECKED
                else -> CHECKED
            }
            updateButton()
        }
        typedArray.recycle()
    }

    override fun setChecked(checked: Boolean) {
        state = if (checked) CHECKED else UNCHECKED
        updateButton()
    }

    private fun updateButton() {
        val btnDrawable = when (state) {
            INDETERMINATE -> R.drawable.ic_checkbox_indeterminate
            UNCHECKED -> R.drawable.ic_checkbox_uncheked
            CHECKED -> R.drawable.ic_checkbox_checked
            else -> R.drawable.ic_checkbox_uncheked
        }
        setButtonDrawable(btnDrawable)
    }

    fun getState(): Int = state

    fun setState(state: Int) {
        this.state = state
        updateButton()
    }

    companion object {
        const val UNCHECKED = 0
        const val INDETERMINATE = 1
        const val CHECKED = 2
    }
}