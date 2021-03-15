package namba.wallet.nambaone.uikit.camera.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@SuppressWarnings("MagicNumber")
internal class ScreenSizeHelper {

    companion object {
        const val SIZE_4x3 = 4f / 3f
    }

    private var displayMetrics = DisplayMetrics()
    private var displaySize = Point(3, 4)

    fun update(context: Context, newConfiguration: Configuration?) {
        val density = context.resources.displayMetrics.density
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (manager != null) {
            val display = manager.defaultDisplay
            if (display != null) {
                display.getMetrics(displayMetrics)
                display.getSize(displaySize)
            }
        }

        var configuration = newConfiguration
        if (configuration == null) configuration = context.resources.configuration
        if (configuration != null) {
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                val newSize = ceil((configuration.screenWidthDp * density).toDouble()).toInt()
                if (abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                val newSize = ceil((configuration.screenHeightDp * density).toDouble()).toInt()
                if (abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize
                }
            }
        }
    }

    fun getScreenRatio(): Float =
        max(displaySize.x.toFloat(), displaySize.y.toFloat()) /
            min(displaySize.x.toFloat(), displaySize.y.toFloat())
}
