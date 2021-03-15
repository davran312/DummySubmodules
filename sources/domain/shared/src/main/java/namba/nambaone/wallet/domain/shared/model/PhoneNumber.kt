package namba.nambaone.wallet.domain.shared.model

import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class PhoneNumber(
    private val rawValue: String,
    private val numberCountryCode: String = DEFAULT_NUMBER_COUNTRY_CODE,
    private val localeCode: String = DEFAULT_LOCALE_CODE
) : Parcelable, UserId {

    companion object {
        const val DEFAULT_NUMBER_COUNTRY_CODE = "996"
        const val DEFAULT_LOCALE_CODE = "KG"
        const val PHONE_NUMBER_LENGTH = 13
        private const val KG_PHONE_NUMBER_PREFIX = "996"
        val EMPTY = PhoneNumber("")
    }

    enum class Validation {
        INCOMPLETE,
        NOT_KG,
        INCORRECT,
        EMPTY,
        SUCCESS
    }

    /**
     * Creates [PhoneNumber.Formatter] instance
     */
    fun format() = PhoneNumberFormatter(this.rawValue)

    @IgnoredOnParcel
    override val value: String =
        if (rawValue.length < PHONE_NUMBER_LENGTH) {
            ""
        } else PhoneNumberUtils.formatNumber(rawValue, localeCode) ?: ""

    @IgnoredOnParcel
    val valueWithoutCountryCode: String = this.value.removePrefix("+$numberCountryCode")

    @IgnoredOnParcel
    val rawWithoutCountryCode: String = this.value.removePrefix("+$numberCountryCode").replace(" ","")

    @IgnoredOnParcel
    val validation: Validation = when {
        value.isNotEmpty() && value
            .trimStart('+')
            .removePrefix(numberCountryCode)
            .startsWith(KG_PHONE_NUMBER_PREFIX, true) -> Validation.NOT_KG
        value.isNotEmpty() -> Validation.SUCCESS
        rawValue.isBlank() || rawValue.trimStart('+').removePrefix(numberCountryCode)
            .isBlank() -> Validation.EMPTY
        rawValue.length == PHONE_NUMBER_LENGTH -> Validation.INCORRECT
        else -> Validation.INCOMPLETE
    }

    @IgnoredOnParcel
    val isValid: Boolean = validation == Validation.SUCCESS

    @IgnoredOnParcel
    val isEmpty: Boolean = validation == Validation.EMPTY
}
