package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import namba.wallet.nambaone.uikit.R
import timber.log.Timber

private const val TITLE_POSITION_BOTTOM = 0
private const val TITLE_POSITION_TOP = 1

class TitledTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleView: TextView
    private val textView: TextView

    init {
        var text: String? = ""
        var title: String? = ""
        var titlePosition: Int = TITLE_POSITION_BOTTOM

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitledTextView)
            text = typedArray.getString(R.styleable.TitledTextView_text)
            title = typedArray.getString(R.styleable.TitledTextView_title)
            titlePosition = typedArray.getInteger(R.styleable.TitledTextView_titlePosition, TITLE_POSITION_BOTTOM)
            typedArray.recycle()
        }

        when (titlePosition) {
            TITLE_POSITION_BOTTOM -> inflate(context, R.layout.widget_titled_text_bottom, this)
            TITLE_POSITION_TOP -> inflate(context, R.layout.widget_titled_text_top, this)
            else -> {
                Timber.e("Unknown title position value: $titlePosition. Falling back to default one")
                inflate(context, R.layout.widget_titled_text_bottom, this)
            }
        }

        orientation = VERTICAL

        textView = findViewById(R.id.textView)
        titleView = findViewById(R.id.topTextView)

        textView.text = text
        titleView.text = title
        titleView.isVisible = !title.isNullOrEmpty()
    }

    var text: String
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    var title: String
        get() = titleView.text.toString()
        set(value) {
            titleView.text = value
            titleView.isVisible = value.isNotEmpty()
        }
}
