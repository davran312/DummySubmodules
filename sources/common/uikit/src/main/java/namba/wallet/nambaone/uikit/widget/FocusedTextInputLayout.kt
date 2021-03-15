package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import namba.wallet.nambaone.common.utils.extensions.afterTextChanged
import namba.wallet.nambaone.uikit.R

open class FocusedTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : TextInputLayout(context, attrs, defStyleAttrs) {

    private var state = InputState()

    init {
        setHelperAndErrorAppearance()
    }

    /*
    * Show Error if user changes focus from the editText,
    * Hide Error if user hasFocus back and if there is an error and user entered something
    * */
    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (child is EditText) {
            state.onFocusChange(child.hasFocus())

            child.setOnFocusChangeListener { _, hasFocus ->
                state.onFocusChange(hasFocus)
                updateData()
            }
            child.afterTextChanged(false) {
                state.onUserInput()
                updateData()
            }
        }
        super.addView(child, params)
    }

    fun setError(@StringRes resId: Int?, isForced: Boolean = false) {
        if (resId == null) {
            clearError()
            return
        }
        state.inputError = InputError(
            text = context.getString(resId),
            isForced = isForced
        )
        updateData()
    }

    fun setError(text: String?, isForced: Boolean = false) {
        if (text == null) {
            clearError()
            return
        }
        state.inputError = InputError(
            text = text,
            isForced = isForced
        )
        updateData()
    }

    fun setHelperText(text: String) {
        state.helperText = text
        updateData()
    }

    fun clearError() {
        state.inputError = null
        updateData()
    }

    fun setLoading(isLoading: Boolean) {
        val text = if (isLoading) context.getString(R.string.remittance_fee_count) else state.helperText
        state.helperText = text
        updateData()
    }

    private fun setHelperAndErrorAppearance() {
        setHelperTextTextAppearance(R.style.AppTheme_HelperTextStyle)
        setErrorTextAppearance(R.style.AppTheme_ErrorTextStyle)
    }

    private fun updateData() {
        state.onState(
            error = { inputError ->
                if (inputError.text != error) {
                    resetHelperAndError()
                    error = inputError.text
                }
            },
            helper = {
                if (it != helperText) {
                    resetHelperAndError()
                    helperText = it
                }
            },
            empty = {
                isErrorEnabled = true
                helperText = null
                error = null
            }
        )
    }

    private fun resetHelperAndError() {
        isErrorEnabled = false
        error = null
        isHelperTextEnabled = false
        helperText = null
    }
}

private data class InputState(
    private var hasFocus: Boolean = false,
    private var hadInputAfterFocus: Boolean = false,
    var inputError: InputError? = null,
    var helperText: String? = null
) {

    fun onFocusChange(hasFocus: Boolean) {
        this.hasFocus = hasFocus
        hadInputAfterFocus = false
    }

    fun onUserInput() {
        hadInputAfterFocus = true
    }

    inline fun onState(
        crossinline error: (InputError) -> Unit,
        crossinline helper: (String) -> Unit,
        crossinline empty: () -> Unit
    ) {
        val currentInputError = inputError
        val currentHelperText = helperText
        when {
            currentInputError != null && (currentInputError.isForced || !hasFocus || hasFocus && !hadInputAfterFocus) ->
                error(currentInputError)
            !currentHelperText.isNullOrEmpty() ->
                helper(currentHelperText)
            else -> empty()
        }
    }
}

private data class InputError(
    val text: String,
    val isForced: Boolean
)
