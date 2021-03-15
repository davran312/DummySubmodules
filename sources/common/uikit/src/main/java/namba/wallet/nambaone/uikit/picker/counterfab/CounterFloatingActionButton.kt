package namba.wallet.nambaone.uikit.picker.counterfab

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import namba.wallet.nambaone.common.utils.extensions.dip
import namba.wallet.nambaone.uikit.R

private const val MARGIN_END = 11 // +1f for TextView border
private const val MARGIN_BOTTOM = 17 // +1f for TextView border
private const val FAB_ANIMATION_DURATION_MILLIS = 150L
private const val FAB_ANIMATION_SCALE_TO = 1.2f

class CounterFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr) {

    private val marginEnd = dip(MARGIN_END)
    private val marginBottom = dip(MARGIN_BOTTOM)
    private val badgeTextView: TextView
    private var badgeTextCount: Int = 0
        set(value) {
            field = value
            updateLayout()
        }

    init {
        // Default values
        var textColor = ContextCompat.getColor(context, android.R.color.white)
        var textBackgroundRes = R.drawable.background_media_checkbox_checked_secondary

        badgeTextView = LayoutInflater.from(context).inflate(R.layout.layout_badge, null) as TextView
        badgeTextView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.CounterFloatingActionButton, defStyleAttr, 0)

            textColor = typedArray.getColor(R.styleable.CounterFloatingActionButton_textColor, textColor)
            textBackgroundRes =
                typedArray.getResourceId(R.styleable.CounterFloatingActionButton_textBackground, textBackgroundRes)

            typedArray.recycle()
        }

        setBadgeTextColor(textColor)
        setBadgeBackground(textBackgroundRes)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (badgeTextCount > 0) {
            canvas.save()
            val x = (measuredWidth - badgeTextView.measuredWidth - marginEnd).toFloat()
            val y = (measuredHeight - badgeTextView.measuredHeight - marginBottom).toFloat()
            canvas.translate(x, y)
            badgeTextView.draw(canvas)
            canvas.restore()
        }
    }

    fun setBadgeTextColor(@ColorInt color: Int) {
        badgeTextView.setTextColor(color)
    }

    fun setBadgeBackground(@DrawableRes backgroundRes: Int) {
        badgeTextView.setBackgroundResource(backgroundRes)
    }

    fun setBadgeCount(itemCount: Int) {
        if (itemCount > 0) {
            if (itemCount != badgeTextCount) {
                animate().cancel()
                animate()
                    .alpha(1f)
                    .setDuration(FAB_ANIMATION_DURATION_MILLIS)
                    .scaleX(FAB_ANIMATION_SCALE_TO)
                    .scaleY(FAB_ANIMATION_SCALE_TO)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withStartAction {
                        isVisible = true
                        badgeTextCount = itemCount
                    }
                    .withEndAction {
                        animate()
                            .setDuration(FAB_ANIMATION_DURATION_MILLIS)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                    .start()
            }
        } else {
            animate().cancel()
            animate()
                .alpha(0f)
                .setDuration(FAB_ANIMATION_DURATION_MILLIS)
                .withEndAction {
                    isInvisible = true
                    badgeTextCount = itemCount
                }
                .start()
        }
    }

    private fun updateLayout() {
        badgeTextView.text = badgeTextCount.toString()
        ensureViewLayout(badgeTextView)
        invalidate()
    }

    private fun ensureViewLayout(view: View) {
        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
