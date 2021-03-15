package namba.wallet.nambaone.uikit.extensions

import android.graphics.Color
import androidx.annotation.ColorInt

@ColorInt
fun colorBetween(@ColorInt startColor: Int, @ColorInt endColor: Int, percent: Float): Int {
    if (startColor == endColor) return startColor

    val startAlpha = Color.alpha(startColor)
    val startRed = Color.red(startColor)
    val startGreen = Color.green(startColor)
    val startBlue = Color.blue(startColor)

    val endAlpha = Color.alpha(endColor)
    val endRed = Color.red(endColor)
    val endGreen = Color.green(endColor)
    val endBlue = Color.blue(endColor)

    return Color.argb(
        ((endAlpha - startAlpha) * percent + startAlpha).toInt(),
        ((endRed - startRed) * percent + startRed).toInt(),
        ((endGreen - startGreen) * percent + startGreen).toInt(),
        ((endBlue - startBlue) * percent + startBlue).toInt()
    )
}
