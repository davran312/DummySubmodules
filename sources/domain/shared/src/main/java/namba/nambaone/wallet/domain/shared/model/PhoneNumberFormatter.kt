package namba.nambaone.wallet.domain.shared.model

import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString


private const val DEFAULT_COUNTRY_CODE = "996"
private const val DEFAULT_FORMAT = "+{$DEFAULT_COUNTRY_CODE} [000] [00] [00] [00]"

class PhoneNumberFormatter(
    private var phoneNumber: String,
    format: String
) {

    constructor() : this("", DEFAULT_FORMAT)
    constructor(phone: String) : this(phone,
        DEFAULT_FORMAT
    )

    private val mask: Mask = Mask.getOrCreate(format, emptyList())

    fun updatePhoneNumber(phone: String) = apply {
        phoneNumber = phone
    }

    fun getMaskResult(caretPosition: Int): Mask.Result {
        val caretString = CaretString(phoneNumber, caretPosition,CaretString.CaretGravity.FORWARD(false))
        return mask.apply(caretString)
    }

    override fun toString(): String =
        with(StringBuilder()) {
            if (phoneNumber.isNotEmpty()) {
                append(getMaskResult(0).formattedText.string)
            }
            return toString()
        }
}
