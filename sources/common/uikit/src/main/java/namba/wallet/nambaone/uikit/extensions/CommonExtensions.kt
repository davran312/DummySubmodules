package namba.wallet.nambaone.uikit.extensions

import android.content.Context

fun coercePercentIn(minValue: Float, maxValue: Float, percent: Float): Float {
    fun normalize(value: Float) = value.coerceIn(0f, 1f)

    val normalizedMin = normalize(minValue)
    val normalizedMax = normalize(maxValue)
    val normalizedPercent = normalize(percent)

    if (normalizedMin >= normalizedMax) throw IllegalStateException()
    val resultPercent = 1 / (normalizedMax - normalizedMin) * (normalizedPercent - normalizedMin)

    return normalize(resultPercent)
}
