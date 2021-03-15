package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import namba.wallet.nambaone.common.utils.extensions.dip
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.resFromTheme


class PageIndicator
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private var roundViews = mutableListOf<ImageView>()
    private var emptyDotDrawableId: Drawable? = null
    private var fullDotDrawableId: Drawable? = null
    private var roundContainer: LinearLayout
    private var currentIndex: Int = 0
    private var pageCount: Int = 0

    init {
        attrs.let {
            emptyDotDrawableId = ContextCompat.getDrawable(context, R.drawable.pin_code_dot_empty)
            fullDotDrawableId = ContextCompat.getDrawable(context, R.drawable.pin_code_dot_full)
        }
        roundContainer = LinearLayout(context)
        roundContainer.apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        addView(roundContainer)
    }

    fun setPage(index: Int): Int {
        currentIndex = index
        for (i in roundViews.indices) {
            if (index == i) {
                roundViews[i].setImageDrawable(fullDotDrawableId)
            } else {
                roundViews[i].setImageDrawable(emptyDotDrawableId)
            }
        }
        return currentIndex
    }

    fun moveLeft(): Int {
        return if (currentIndex == 0) setPage(pageCount -1)
        else setPage(--currentIndex)
    }

    fun moveRight(): Int {
        return if (currentIndex < pageCount - 1) setPage(++currentIndex)
        else setPage(0)
    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        roundContainer.removeAllViews()
        val temp = mutableListOf<ImageView>()
        for (i in 0 until pageCount) {
            val roundView = if (i < roundViews.size) {
                roundViews[i]
            } else {
                inflater.inflate(R.layout.pin_code_dot_view, roundContainer, false) as ImageView
            }
            val params = roundView.layoutParams as LinearLayout.LayoutParams
            params.width = dip(8)
            params.height = dip(8)
            params.setMargins(4,4,4,4)
            roundView.layoutParams = params
            roundContainer.addView(roundView)
            temp.add(roundView)
        }
        roundViews.clear()
        roundViews.addAll(temp)
    }
}
