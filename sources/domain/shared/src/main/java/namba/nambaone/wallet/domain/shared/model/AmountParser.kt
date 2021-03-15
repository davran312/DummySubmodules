package namba.nambaone.wallet.domain.shared.model

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Used for [Amount] instance creation
 */
object AmountParser {

    private val SYMBOLS = DecimalFormatSymbols().apply {
        minusSign = Amount.MINUS
        decimalSeparator = Amount.DELIMITER
        groupingSeparator = ' '
    }

    private val PARSE_FORMAT = object : ThreadLocal<DecimalFormat>() {
        override fun initialValue(): DecimalFormat =
            DecimalFormat("#,##0.##", SYMBOLS).apply { isParseBigDecimal = true }
    }

    /**
     *
     */
    fun parse(
        units: Long,
        nanos: Int,
        nanosScale: BigDecimal = Amount.DEFAULT_NANOS_SCALE,
        currencyCode: String = Amount.DEFAULT_CURRENCY_CODE
    ): Amount {
        val unitsBig = BigDecimal(units)
        val nanosBig = BigDecimal(nanos).divide(nanosScale)

        return Amount(
            unitsBig.add(nanosBig),
            currencyCode,
            nanosScale
        )
    }

    /**
     * Creates [Amount] instance
     * @param value amount value, in '-1 234.56 $' format
     * @param currencyCode three letter currency code (ISO 4217), KZT by default
     * @return [Amount] instance, if value is empty or equals "." return zero amount
     */
    fun parse(
        value: String,
        nanosScale: BigDecimal = Amount.DEFAULT_NANOS_SCALE,
        currencyCode: String = Amount.DEFAULT_CURRENCY_CODE
    ): Amount {
        var safeValue = value
            .replace(Amount.ALTERNATIVE_DELIMITER, Amount.DELIMITER)
            .replace(" ", "")
            .replace(Amount.NBSP.toString(), "")

        if (safeValue.isEmpty() || safeValue == "") safeValue = "0"
        return Amount(
            value = PARSE_FORMAT.get()?.parse(safeValue) as BigDecimal,
            currencyCode = currencyCode,
            nanosScale = nanosScale
        )
    }

    /**
     * Creates [Amount] instance
     * @param value amount value
     * @param currencyCode three letter currency code (ISO 4217), KZT by default
     */
    fun parse(
        value: Double,
        nanosScale: BigDecimal = Amount.DEFAULT_NANOS_SCALE,
        currencyCode: String = Amount.DEFAULT_CURRENCY_CODE
    ) =
        Amount(
            value = BigDecimal.valueOf(value),
            currencyCode = currencyCode,
            nanosScale = nanosScale
        )
}
