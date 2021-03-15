package namba.nambaone.wallet.domain.shared.model

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Used for formatting [Amount] as string
 *
 * By default:
 * - cents is shown
 * - plus sing is NOT shown
 * - currency symbol is shown
 */
class AmountFormatter(val amount: Amount) {
    private var isCentsVisible: Boolean = true

    private var isPlusSignVisible: Boolean = false

    private var isCurrencySymbolVisible: Boolean = true

    private var areDecimalZerosVisible = false

    private var isAbsoluteValue = false


    /**
     * Hide cents via rounding amount to integer
     */
    fun hideCents() = apply { isCentsVisible = false }

    /**
     * Show plus sign for positive amount
     */
    fun showPlusSign() = apply { isPlusSignVisible = true }

    fun showAbsoluteValue() = apply { isAbsoluteValue = true }

    /**
     * Shows decimal zeros, even if nanos are empty.
     */
    fun showDecimalZeros() = apply { areDecimalZerosVisible = true }

    /**
     * Hide currency symbol
     */
    fun hideCurrencySymbol() = apply { isCurrencySymbolVisible = false }

    override fun toString(): String = buildString {
        if (amount.isNegative and !isAbsoluteValue) {
            append("${Amount.MINUS}${Amount.NBSP}")
        } else if (isPlusSignVisible) {
            append("${Amount.PLUS}${Amount.NBSP}")
        }

        if (isCentsVisible) {
            append(DEFAULT_FORMAT.get()?.format(amount.value))
        } else {
            append(INTEGER_FORMAT.get()?.format(amount.value))
        }

        if (!areDecimalZerosVisible) {
            val doubleZeros = "${Amount.DELIMITER}00"
            if (endsWith(doubleZeros)) setLength(length - doubleZeros.length)

            val singleZero = "${Amount.DELIMITER}0"
            if (endsWith(singleZero)) setLength(length - singleZero.length)
        }

        if (isCurrencySymbolVisible) append("${Amount.NBSP}${Amount.getCurrencySymbol(amount.currencyCode)}")
    }

    /**
     * Creates [AmountDecorator] instance
     */
    fun decorate(): AmountDecorator = AmountDecorator(this)

    companion object {
        private val SYMBOLS = DecimalFormatSymbols().apply {
            decimalSeparator = Amount.DELIMITER
            groupingSeparator = Amount.NBSP
        }
        private val DEFAULT_FORMAT = object : ThreadLocal<DecimalFormat>() {
            override fun initialValue(): DecimalFormat = DecimalFormat("#,##0.00", SYMBOLS).apply {
                negativePrefix = ""
                positivePrefix = ""
            }
        }
        private val INTEGER_FORMAT = object : ThreadLocal<DecimalFormat>() {
            override fun initialValue(): DecimalFormat =
                DecimalFormat("#,##0", SYMBOLS).apply { roundingMode = RoundingMode.DOWN }.apply {
                    negativePrefix = ""
                    positivePrefix = ""
                }
        }
    }
}
