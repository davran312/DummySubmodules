package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.widget_keyboard.view.additionalLeftPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.additionalRightPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.eightPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.fivePinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.fourPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.ninePinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.onePinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.sevenPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.sixPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.threePinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.twoPinCodeButton
import kotlinx.android.synthetic.main.widget_keyboard.view.zeroPinCodeButton
import namba.wallet.nambaone.uikit.R

class NumberKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var onNumberClicked: ((Char) -> Unit)? = null
    var onLeftButtonClicked: (() -> Unit)? = null
    var onRightButtonClicked: (() -> Unit)? = null

    private var buttons = mutableListOf<NumberKeyboardButtonView>()

    init {
        inflate(context, R.layout.widget_keyboard, this)

        initKeyboardButtons()
    }

    private fun initKeyboardButtons() {
        buttons.apply {
            add(zeroPinCodeButton)
            add(onePinCodeButton)
            add(twoPinCodeButton)
            add(threePinCodeButton)
            add(fourPinCodeButton)
            add(fivePinCodeButton)
            add(sixPinCodeButton)
            add(sevenPinCodeButton)
            add(eightPinCodeButton)
            add(ninePinCodeButton)
            add(additionalRightPinCodeButton)
            add(additionalLeftPinCodeButton)
        }

        buttons.forEach {
            it.setOnClickListener { view -> onClick(view) }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.additionalLeftPinCodeButton -> onLeftButtonClicked?.invoke()
            R.id.additionalRightPinCodeButton -> onRightButtonClicked?.invoke()
            else -> {
                val char = when (view.id) {
                    R.id.zeroPinCodeButton -> '0'
                    R.id.onePinCodeButton -> '1'
                    R.id.twoPinCodeButton -> '2'
                    R.id.threePinCodeButton -> '3'
                    R.id.fourPinCodeButton -> '4'
                    R.id.fivePinCodeButton -> '5'
                    R.id.sixPinCodeButton -> '6'
                    R.id.sevenPinCodeButton -> '7'
                    R.id.eightPinCodeButton -> '8'
                    R.id.ninePinCodeButton -> '9'
                    else -> throw IllegalArgumentException("ID does not reference a View inside this View")
                }

                onNumberClicked?.invoke(char)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        buttons.forEach { it.isEnabled = enabled }
        super.setEnabled(enabled)
    }

    fun setLeftButtonVisible(isVisible: Boolean) {
        additionalLeftPinCodeButton.isInvisible = !isVisible
    }

    fun setLeftButtonDrawable(@DrawableRes drawableResId: Int) {
        additionalLeftPinCodeButton.setIcon(drawableResId)
    }

    fun setRightButtonVisible(isVisible: Boolean) {
        additionalRightPinCodeButton.isInvisible = !isVisible
    }

    fun setRightButtonDrawable(@DrawableRes drawableResId: Int) {
        additionalRightPinCodeButton.setIcon(drawableResId)
    }
}
