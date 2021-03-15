package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.toolbarx.view.*
import namba.wallet.nambaone.common.utils.extensions.dip
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.colorFromTheme

class NambaToolbar(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.toolbarx, this)
        orientation = VERTICAL
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NambaToolbar)
        val navigationTint = ta.getColor(
            R.styleable.NambaToolbar_navigationTint,
            colorFromTheme(R.attr.colorHighEmphasisOnPrimary)
        )
        val backgroundResource = ta.getColor(
            R.styleable.NambaToolbar_toolbarBackground,
            colorFromTheme(R.attr.colorPrimary)
        )
        val elevated = ta.getBoolean(
            R.styleable.NambaToolbar_elevated,
            true
        )
        shadow.isVisible = elevated

        setBackgroundColor(backgroundResource)

        val arrow = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_black)!!
        val wrappedDrawable = DrawableCompat.wrap(arrow).mutate()
        DrawableCompat.setTint(wrappedDrawable, navigationTint)

        toolbar.setBackgroundColor(backgroundResource)
        toolbar.setTitleTextColor(navigationTint)
        ta.recycle()
    }

    inline operator fun invoke(crossinline block: Toolbar.() -> Unit) {
        toolbar.block()
        centerTitle(toolbar)
    }

    fun setNavigationIcon(@DrawableRes icon: Int) {
        toolbar.navigationIcon = ContextCompat.getDrawable(context, icon)
    }

    fun centerTitle(toolbar: Toolbar) {
        val title = toolbar.title
        val outViews = arrayListOf<View>()
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT)
        if (outViews.isNotEmpty()) {
            val title = outViews[0] as TextView
            title.gravity = Gravity.CENTER
            val params = title.layoutParams as Toolbar.LayoutParams
            params.marginEnd = dip(toolbar.navigationIcon?.intrinsicWidth?: 0)
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            toolbar.requestLayout()
        }
    }
}
