package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import namba.wallet.nambaone.uikit.R

open class RoundedConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {
    private var path: Path = Path()
    private var cornerRadius = 0f

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout)
        cornerRadius = ta.getDimension(R.styleable.RoundedFrameLayout_android_radius, 0f)
        ta.recycle()

        outlineProvider = OutlineProvider(cornerRadius)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.addRoundRect(
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saved = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saved)
        super.dispatchDraw(canvas)
    }

    private class OutlineProvider(private val radius: Float) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
            view.clipToOutline = true
        }
    }
}