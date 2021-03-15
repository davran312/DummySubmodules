package namba.wallet.nambaone.common.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.EditText
import namba.wallet.nambaone.common.utils.CustomMaskTextChangedListener
import namba.wallet.nambaone.common.exception.ConnectionException
import namba.wallet.nambaone.common.network.errors.ServerException
import namba.wallet.nambaone.core.R
import timber.log.Timber

fun Throwable?.getErrorMessage(context: Context): String =
    when (this) {
        is ConnectionException -> context.getString(R.string.connection_error_message)
        is ServerException -> getErrorMessage(context)
        else -> context.getString(R.string.general_error_message)
    }

@SuppressLint("ClickableViewAccessibility")
fun EditText.onDrawableClicked(
    onRightClicked: ((view: EditText) -> Unit)? = null,
    onLeftClicked: ((view: EditText) -> Unit)? = null
) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onRightClicked?.invoke(this)
                }
                hasConsumed = true
            } else if (event.x <= v.totalPaddingLeft) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onLeftClicked?.invoke(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun EditText.setupMaskListener(mask: String, afterChanged: (extractedValue: String) -> Unit) {
    val maskListener = object : CustomMaskTextChangedListener.ValueListener {
        override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
            afterChanged(extractedValue)
        }
    }
    val listener = CustomMaskTextChangedListener.installOn(this, mask, maskListener)

    this.hint = listener.placeholder(charRepresentation = 'X')
}

fun <T> MutableList<T>.appendOrRemove(item: T, predicate: Boolean) = if (
    predicate) remove(item) else add(item)

fun <T> List<T>?.countOrZero(): Int =
    this?.count() ?: 0

fun <T, R> Pair<T, R>.containsNotNull(): Boolean = this.first != null || this.second != null



