package namba.wallet.nambaone.common.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.CharacterStyle

object TextUtils {

    @SuppressWarnings("ReturnCount")
    fun applySpanToSubstring(source: String, substring: String, characterStyle: CharacterStyle): CharSequence {
        if (substring.isBlank()) return source

        val spacedSource = " $source"
        val spacedSubstring = " $substring"
        var index = spacedSource.indexOf(spacedSubstring, ignoreCase = true)
        if (index == -1) return source

        val spannedSource = SpannableString(source)
        while (index != -1) {
            val end = index + substring.length
            spannedSource.setSpan(characterStyle, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            index = spacedSource.indexOf(spacedSubstring, startIndex = end, ignoreCase = true)
        }
        return spannedSource
    }
}
