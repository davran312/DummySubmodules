package namba.nambaone.wallet.domain.shared.model

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import namba.nambaone.wallet.domain.shared.model.Amount.Companion.DELIMITER
import namba.nambaone.wallet.domain.shared.model.Amount.Companion.NBSP

/**
 * Used for decorating [Amount] string with font weights, colors, etc.
 */
class AmountDecorator(amountFormatter: AmountFormatter) :
    SpannableString(amountFormatter.toString()) {

    fun boldUnits() = apply {
        val lastIndexOfSeparator = lastIndexOf(DELIMITER).takeIf { it != -1 }
            ?: lastIndexOf(NBSP).takeIf { it != -1 }
            ?: length
        setSpan(StyleSpan(Typeface.BOLD), 0, lastIndexOfSeparator, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }

}
