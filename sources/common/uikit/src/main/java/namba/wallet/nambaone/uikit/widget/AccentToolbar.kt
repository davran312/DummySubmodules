package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.android.synthetic.main.toolbarx.view.*
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.colorFromTheme

class AccentToolbar(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.toolbarx, this)
        orientation = VERTICAL

        setBackgroundColor(colorFromTheme(R.attr.colorAccent))

        val arrow = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_black)!!
        val wrappedDrawable = DrawableCompat.wrap(arrow).mutate()
        DrawableCompat.setTint(wrappedDrawable, Color.WHITE)

        toolbar.navigationIcon = wrappedDrawable
        toolbar.setBackgroundColor(colorFromTheme(R.attr.colorAccent))
        toolbar.setTitleTextColor(Color.WHITE)
    }

    inline operator fun invoke(crossinline block: Toolbar.() -> Unit) {
        toolbar.block()
    }

    fun setNavigationIcon(@DrawableRes icon: Int) {
        toolbar.navigationIcon = ContextCompat.getDrawable(context, icon)
    }
}
