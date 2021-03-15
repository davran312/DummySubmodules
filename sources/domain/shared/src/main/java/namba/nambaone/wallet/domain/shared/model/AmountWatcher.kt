package namba.nambaone.wallet.domain.shared.model

import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import namba.wallet.nambaone.wallet.domain.shared.R
import java.lang.ref.WeakReference
import kotlin.properties.Delegates.observable

private const val MAX_NUMBER_OF_CHARS = 17
private const val MAX_UNIT_SYMBOLS = 11
private const val MAX_NANOS_SYMBOLS = 2
private const val AMOUNT_FORMAT = "[000] [000] [000]"

class AmountWatcher(
    editText: EditText,
    private val currencyCode: String = Amount.DEFAULT_CURRENCY_CODE,
    private val valueListener: ((Amount) -> Unit)?
) : TextWatcher, View.OnFocusChangeListener {

    companion object {
        fun installOn(
            editText: EditText,
            currencyCode: String = Amount.DEFAULT_CURRENCY_CODE,
            valueChangedCallback: ((Amount) -> Unit)? = null
        ): AmountWatcher {
            editText.filters = arrayOf(InputFilter.LengthFilter(MAX_NUMBER_OF_CHARS))
            val currencySymbol = Amount.getCurrencySymbol(currencyCode)
            val amountWatcher = AmountWatcher(editText, currencyCode, valueChangedCallback)
            editText.addTextChangedListener(amountWatcher)
            editText.setTag(R.id.amount_watcher_tag_id, amountWatcher)
            editText.keyListener = DigitsKeyListener.getInstance("1234567890,. $currencySymbol")
            OnFocusChangedListenerWrapper.installOn(editText, amountWatcher)

            editText.setTypeface(editText.typeface, Typeface.NORMAL)

            return amountWatcher
        }

        fun uninstallFrom(editText: EditText) {
            val amountWatcher = editText.getTag(R.id.amount_watcher_tag_id) as? AmountWatcher
            editText.removeTextChangedListener(amountWatcher)
            OnFocusChangedListenerWrapper.uninstallWrappersChain(editText)
        }
    }

    private val currencySymbol: String = Amount.getCurrencySymbol(currencyCode)
    private val zeroAmount = Amount.zero(currencyCode)
    private val nanosTrimRegexp = Regex("[\\s$currencySymbol]")
    private val mask: Mask = Mask.getOrCreate(AMOUNT_FORMAT, emptyList())
    private val field = WeakReference(editText)
    private var caretPosition: Int = 0
    private var amountText: AmountText by observable(AmountText()) { _, oldValue, newValue ->
        if (oldValue.extracted != newValue.extracted) {
            val finalValue = newValue.extracted
            val amount = if (finalValue.isEmpty()) {
                zeroAmount
            } else {
                AmountParser.parse(value = finalValue, currencyCode = currencyCode)
            }
            valueListener?.invoke(amount)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing here.
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) return
        field.get()?.apply {
            var resultText = amountText.masked

            if (amountText.nanos.isNotEmpty()) {
                resultText = resultText.trimEnd('0')
            }

            if (resultText.endsWith(Amount.DELIMITER)) {
                resultText = resultText.trimEnd(Amount.DELIMITER)
            }

            if (resultText != amountText.masked) {
                setText(resultText)
            }
        }
    }

    override fun afterTextChanged(edit: Editable?) {
        field.get()?.apply {
            removeTextChangedListener(this@AmountWatcher)
            val text = buildString {
                append(amountText.masked)
                if (amountText.isNotEmpty) {
                    append(' ')
                    append(currencySymbol)
                }
            }
            edit?.replace(0, edit.length, text)

            setTypeface(typeface, Typeface.NORMAL)
            setSelection(caretPosition)
            addTextChangedListener(this@AmountWatcher)
        }
    }

    @Suppress("ReturnCount")
    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {

        val textString =
            text.toString()
                .removeSuffix(currencySymbol)
                .trimEnd(' ')
                .replace(Amount.ALTERNATIVE_DELIMITER, Amount.DELIMITER)

        // If text is empty - then set amount text as empty, drop caret position to zero and exit.
        if (textString.isEmpty()) {
            amountText = AmountText()
            caretPosition = 0
            return
        }

        // If text is just a delimiter then set up amount text units to 0, append delimiter and exit.
        if (textString == Amount.DELIMITER.toString()) {
            amountText = AmountText("0", appendDelimiter = true)
            caretPosition = amountText.masked.length
            return
        }

        // If there is more that one delimiter in input then save caret position and drop that input.
        if (text.count { it == Amount.DELIMITER } > 1) {
            caretPosition = start
            return
        }

        val split = textString.split(Amount.DELIMITER)

        val units = split.getOrNull(0) ?: ""
        val nanos = split.getOrNull(1) ?: ""
        val trimmedUnits = units.trimStart('0', ' ')
        val trimmedNanos = nanos.replace(nanosTrimRegexp, "")

        // If units or nanos part of input is overflowed then save caret position and drop that input.
        if (trimmedUnits.length > MAX_UNIT_SYMBOLS || trimmedNanos.length > MAX_NANOS_SYMBOLS) {
            caretPosition = start
            return
        }

        // At that point input is verified and validated.
        val initialDelimiterPosition = textString.indexOf(Amount.DELIMITER)
        val isDeletion: Boolean = before > 0 && count == 0

        val initialCaret = if (isDeletion) start else start + count

        val trimmedCaret = initialCaret - units.length + trimmedUnits.length

        val (maskedUnit, maskedCaret) = applyMaskReversed(trimmedUnits, trimmedCaret)
        val isSplittingUnits = !amountText.endsWithDelimiter && amountText.nanos.isEmpty() && nanos.isNotEmpty()
        val caretOffset = if (isSplittingUnits) initialCaret - maskedCaret else 0

        val shouldAppendDelimiter = trimmedNanos.isNotEmpty() || initialDelimiterPosition != -1
        val resultUnits = maskedUnit.ifEmpty { if (shouldAppendDelimiter) "0" else "" }
        amountText = AmountText(resultUnits, trimmedNanos, shouldAppendDelimiter)

        val isCaretInUnits = initialDelimiterPosition == -1 || initialCaret <= initialDelimiterPosition
        val resultCaret = (if (isCaretInUnits) maskedCaret else initialCaret) - caretOffset
        caretPosition = resultCaret.coerceIn(0, amountText.masked.length)
    }

    private fun applyMaskReversed(string: String, caretPosition: Int): Pair<String, Int> {
        val result: Mask.Result = mask.apply(
            CaretString(string.reversed(), string.length - caretPosition,
            CaretString.CaretGravity.FORWARD(false))
        )

        return result.formattedText.let {
            it.string.reversed() to it.string.length - it.caretPosition
        }
    }
}

private data class AmountText(
    val units: String = "",
    val nanos: String = "",
    val appendDelimiter: Boolean = false
) {
    val masked = buildString {
        append(units)
        if (appendDelimiter) append(Amount.DELIMITER)
        append(nanos)
    }

    val extracted = buildString {
        append(units.replace(" ", ""))
        if (nanos.any { it != '0' }) {
            append(Amount.DELIMITER)
            append(nanos)
        }
    }

    val isEmpty = units.isEmpty() && nanos.isEmpty()

    val isNotEmpty = !isEmpty

    val endsWithDelimiter = masked.endsWith(Amount.DELIMITER)
}
