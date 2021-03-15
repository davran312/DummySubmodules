package namba.nambaone.wallet.domain.shared.model

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@SuppressWarnings("DataClassContainsFunctions")
@Parcelize
data class Amount(
    val value: BigDecimal,
    val currencyCode: String = DEFAULT_CURRENCY_CODE,
    val nanosScale: BigDecimal = DEFAULT_NANOS_SCALE
) : Parcelable {

    constructor(v: Long, currencyCode: String = DEFAULT_CURRENCY_CODE) : this(
        value = BigDecimal.valueOf(v),
        currencyCode = currencyCode,
        nanosScale = DEFAULT_NANOS_SCALE
    )


    constructor(source: Parcel) : this(
        value = (source.readSerializable() as BigDecimal),
        currencyCode = source.readString() ?: DEFAULT_CURRENCY_CODE,
        nanosScale = source.readSerializable() as BigDecimal
    )

    constructor(double: Double) : this(BigDecimal.valueOf(double))

    @IgnoredOnParcel
    val isNegative = value.signum() < 0

    @IgnoredOnParcel
    val isZero = value.signum() == 0

    @IgnoredOnParcel
    val isPositive = value.signum() > 0

    @IgnoredOnParcel
    val isRuble = currencyCode.equals(RUR, ignoreCase = true) ||
        currencyCode.equals(RUB, ignoreCase = true)

    @IgnoredOnParcel
    val units by lazy { value.toBigInteger().toLong() }

    /**
     * Nanos value sclaed using [nanosScale] passed during amount creation.
     */
    @IgnoredOnParcel
    val nanos by lazy { nanos(nanosScale) }

    /**
     * Nanos value scaled using passed [scale].
     */
    fun nanos(scale: BigDecimal) = value.rem(BigDecimal.ONE)
        .multiply(scale)
        .toBigInteger()
        .toInt()

    /**
     * Creates [Amount.Formatter] instance
     */
    fun format() = AmountFormatter(this)

    fun negative() = copy(value = value.negate())

    fun toCurrency(currencyCode: String = "KG"): Amount {
        return when (currencyCode) {
            "KG" -> this.times(0.01)
            else -> this.times(0.01)
        }
    }

    operator fun plus(other: Amount): Amount {
        assertCurrencyEquals(other)
        return copy(value = value.plus(other.value))
    }

    operator fun minus(other: Amount): Amount {
        assertCurrencyEquals(other)
        return copy(value = value.minus(other.value))
    }

    operator fun times(multiplicand: Amount): Amount {
        assertCurrencyEquals(multiplicand)
        return copy(value = value.multiply(multiplicand.value))
    }

    operator fun times(multiplicand: Int) =
        copy(value = value.multiply(multiplicand.toBigDecimal()))

    operator fun times(multiplicand: Double) =
        copy(value = value.multiply(multiplicand.toBigDecimal()))

    operator fun div(divisor: Amount): Amount {
        assertCurrencyEquals(divisor)
        return copy(value = value.divide(divisor.value))
    }

    operator fun div(divisor: Int) = copy(value = value.divide(divisor.toBigDecimal()))

    operator fun div(divisor: Double) = copy(value = value.divide(divisor.toBigDecimal()))

    operator fun compareTo(other: Amount): Int {
        assertCurrencyEquals(other)
        return value.compareTo(other.value)
    }

    fun abs() = copy(value = value.abs())

    private fun assertCurrencyEquals(value: Amount) {
        if (!(isRuble && value.isRuble) && currencyCode != value.currencyCode) {
            throw IllegalArgumentException("Amount must have same currency. Got $currencyCode|${value.currencyCode}")
        }
    }

    companion object {

        const val MINUS = '\u2212'
        const val PLUS = '\u002B'
        const val NBSP = '\u00A0'

        const val KG = "KG"
        const val KZT = "KZT"
        const val RUB = "RUB"
        const val RUR = "RUR"
        const val USD = "USD"
        const val EUR = "EUR"

        // TODO: Implement dynamic locale dependant separator support.
        const val DELIMITER = ','

        const val ALTERNATIVE_DELIMITER = '.'

        private var defaults: AmountDefaults? = null

        val DEFAULT_NANOS_SCALE: BigDecimal = BigDecimal.valueOf(1_000_000_000L)

        val DEFAULT_CURRENCY_CODE: String by lazy {
            if (defaults == null) Timber.w("Amount defaults are not initialized. Falling back to KZT.")
            defaults?.defaultCurrencyCode ?: KG
        }

        val ZERO by lazy { zero(DEFAULT_CURRENCY_CODE) }

        fun init(defaultCurrencyCode: String) {
            defaults = AmountDefaults(defaultCurrencyCode)
        }

        fun zero(currencyCode: String): Amount = Amount(BigDecimal.ZERO, currencyCode)

        fun getCurrencySymbol(currencyCode: String): String =
            when (currencyCode) {
                "RUR" -> "₽"
                "RUB" -> "₽"
                "USD" -> "$"
                "EUR" -> "€"
                "CNY" -> "元"
                "GBP" -> "£"
                "KZT" -> "₸"
                "JPY" -> "¥"
                "BYR" -> "B"
                "CHF" -> "₣"
                "UAH" -> "₴"
                "KG" -> "c"

                else -> currencyCode
            }

        @SuppressWarnings("MagicNumber")
        fun getCurrencyNumericCode(currencyCode: String) =
            when (currencyCode) {
                "RUR" -> 810
                "RUB" -> 643
                "USD" -> 840
                "EUR" -> 978
                "CNY" -> 156
                "GBP" -> 826
                "KZT" -> 398
                "JPY" -> 392
                "BYR" -> 974
                "CHF" -> 756
                "UAH" -> 980
                else -> 0
            }
    }

    private data class AmountDefaults(
        val defaultCurrencyCode: String
    )
}
