package namba.wallet.nambaone.uikit.camera.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.LifecycleObserver
import namba.wallet.nambaone.uikit.R

class RecordButtonView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.imageButtonStyle) :
    AppCompatImageButton(context, attrs, defStyleAttr),
    LifecycleObserver {

    var gestureListener: (() -> Unit)? = null

    fun initListeners() {
        val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                gestureListener?.invoke()
                return false
            }
        })

        gestureDetector.setIsLongpressEnabled(false)
        setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            super.onTouchEvent(event)
        }
    }
}
