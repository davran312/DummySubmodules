package namba.wallet.nambaone.common.ui.watcher

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference
import kotlin.properties.Delegates
import namba.wallet.nambaone.core.R

private const val SPACE_SYMBOL = " "
private const val REGEX_TRIM_PATTERN = "\\s{2,}"

class TrimmingTextWatcher(
    editText: EditText,
    private val valueListener: ((String) -> Unit)?
) : TextWatcher {

    companion object {
        fun installOn(
            editText: EditText,
            valueListener: ((String) -> Unit)? = null
        ): TrimmingTextWatcher {
            val trimmingWatcher = TrimmingTextWatcher(editText, valueListener)
            editText.addTextChangedListener(trimmingWatcher)
            editText.setTag(R.id.trimming_watcher_tag_id, trimmingWatcher)
            editText.setTypeface(editText.typeface, if (editText.text.isEmpty()) Typeface.NORMAL else Typeface.BOLD)
            return trimmingWatcher
        }
    }

    private val field = WeakReference(editText)
    private var valueText: String by Delegates.observable("") { _, oldValue, newValue ->
        if (oldValue != newValue) valueListener?.invoke(newValue)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing here.
    }

    override fun afterTextChanged(edit: Editable?) {
        this.field.get()?.removeTextChangedListener(this)
        edit?.replace(0, edit.length, valueText)
        this.field.get()?.addTextChangedListener(this)
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        valueText = text.toString().replace(Regex(REGEX_TRIM_PATTERN), SPACE_SYMBOL).trimStart()
    }
}
