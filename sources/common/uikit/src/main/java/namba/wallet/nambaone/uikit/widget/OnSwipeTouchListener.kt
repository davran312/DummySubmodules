package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import timber.log.Timber
import kotlin.math.abs

enum class SwipeDirection {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM
}

class OnSwipeTouchListener(context: Context, callback: (SwipeDirection) -> Unit) :
    View.OnTouchListener {

    private var gestureDetector: GestureDetector =
        GestureDetector(context, GestureListener(callback))

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(p1)
    }
    private class GestureListener(private val callback: (SwipeDirection) -> Unit) :
        GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0)
                            callback.invoke(SwipeDirection.RIGHT)
                        else
                            callback.invoke(SwipeDirection.LEFT)

                    }
                    result = true
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            return result
        }
    }
}