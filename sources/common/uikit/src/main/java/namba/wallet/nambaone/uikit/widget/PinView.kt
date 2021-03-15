package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.os.postDelayed
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.widget_pin_view.view.keyboardView
import kotlinx.android.synthetic.main.widget_pin_view.view.pinCodeView
import kotlinx.android.synthetic.main.widget_pin_view.view.progressBar
import namba.wallet.nambaone.uikit.R

private const val PIN_CODE_LENGTH = 6
private const val DELAY_MS = 100L

class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var onPinCompleted: ((String) -> Unit)? = null
    var onBiometricClicked: (() -> Unit)? = null

    var pinCode: String = ""
    private set

    private val pinHandler = Handler()

    init {
        inflate(context, R.layout.widget_pin_view, this)
        orientation = VERTICAL

        pinCodeView.setPinLength(PIN_CODE_LENGTH)

        keyboardView.setLeftButtonVisible(false)
        keyboardView.setLeftButtonDrawable(R.drawable.ic_fingerprint)
        keyboardView.setRightButtonDrawable(R.drawable.ic_remove)

        keyboardView.onNumberClicked = { number -> onNumberEntered(number) }

        keyboardView.onLeftButtonClicked = {
            onBiometricClicked?.invoke()
        }

        keyboardView.onRightButtonClicked = {
            pinCode = pinCode.dropLast(1)
            updateDots()
        }
    }

    fun startErrorAnimation(onFinish: () -> Unit) {
        pinCodeView.startErrorAnimation {
            onFinish.invoke()
        }
        clearPin()
    }

    fun setFingerprintVisible(isVisible: Boolean) {
        keyboardView.setLeftButtonVisible(isVisible)
    }

    fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        pinCodeView.isInvisible = isLoading
        keyboardView.isEnabled = !isLoading
    }

    fun clearPin() {
        pinCode = ""
        updateDots()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        keyboardView.isEnabled = enabled
    }

    private fun onNumberEntered(number: Char) {
        if (pinCode.length == PIN_CODE_LENGTH) return

        if (pinCode.length < PIN_CODE_LENGTH) {
            pinCode += number
            updateDots()
        }

        if (pinCode.length == PIN_CODE_LENGTH) {
            pinHandler.postDelayed(DELAY_MS) { onPinCompleted?.invoke(pinCode) }
        }
    }

    private fun updateDots() {
        pinCodeView.refresh(pinCode.length)
        keyboardView.setRightButtonVisible(pinCode.isNotEmpty())
    }
}
