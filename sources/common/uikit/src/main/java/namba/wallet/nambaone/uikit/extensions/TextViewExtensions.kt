package namba.wallet.nambaone.uikit.extensions

import android.graphics.BlurMaskFilter
import android.widget.TextView


fun TextView.setBlurEnabled(isEnabled: Boolean, radius: Float = textSize / 3) {
    paint.maskFilter = if (isEnabled) {
        BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
    } else {
        null
    }
    invalidate()
}
