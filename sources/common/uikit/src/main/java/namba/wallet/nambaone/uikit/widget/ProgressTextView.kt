package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.widget_progress_text_view.view.progressBar
import kotlinx.android.synthetic.main.widget_progress_text_view.view.textView
import namba.wallet.nambaone.uikit.R

private const val TRANSITION_DELAY_MS = 100L
private const val FADE_OUT_DURATION_MS = 100L
private const val FADE_IN_DURATION_MS = 150L

class ProgressTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.widget_progress_text_view, this)
    }

    fun setText(value: String) {
        textView.text = value
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            crossFade(textView, progressBar)
        } else {
            crossFade(progressBar, textView)
        }
    }

    private fun crossFade(fadeOutView: View, fadeInView: View) {
        fadeOutView.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_DURATION_MS)
            .setStartDelay(TRANSITION_DELAY_MS)

        fadeInView.animate()
            .alpha(1f)
            .setDuration(FADE_IN_DURATION_MS)
            .setStartDelay(TRANSITION_DELAY_MS)
    }
}
