package namba.wallet.nambaone.uikit.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import namba.wallet.nambaone.uikit.R
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

/**
 * Fixes a problem with fitsSystemWindows where only the first view in viewHierarchy has a chance
 * to offset itself away from Status and Navigation bars. Typically when a view is defined
 * to fitsSystemWindows, it consumes those offsets, leaving no offsets for other views.
 * Listen for dispatch of WindowInsets and redispatch the offsets to all children. Even if the first
 * child consumes offsets, other children get the original offsets and can react accordingly as well.
 */
fun ViewGroup.redispatchWindowInsetsToAllChildren() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view as ViewGroup
        var consumed = false

        Timber.tag("insets").d(view::class.java.simpleName)
        view.children.forEach { child ->
            // Dispatch the insets to the child
            val childResult = ViewCompat.dispatchApplyWindowInsets(child, insets)
            // If the child consumed the insets, record it
            if (childResult.isConsumed) consumed = true
        }
        // If any of the children consumed the insets, return an appropriate value
        if (consumed) insets.consumeSystemWindowInsets() else insets
    }
}

fun View.getTintedDrawable(@DrawableRes drawableRes: Int, @ColorInt tintColor: Int) =
    context.getTintedDrawable(drawableRes, tintColor)

fun ViewPager.addOnPageSelectedListener(listener: (position: Int) -> Unit) {
    val pageListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) = Unit

        override fun onPageSelected(position: Int) {
            listener(position)
        }
    }
    doOnAttach { addOnPageChangeListener(pageListener) }
    doOnDetach { removeOnPageChangeListener(pageListener) }
}

fun Context.showDatePicker(
    date: Calendar = Calendar.getInstance(),
    listener: DatePickerDialog.OnDateSetListener
): DatePickerDialog {
    val dialog = DatePickerDialog(
        this,
        R.style.DialogTheme,
        listener,
        date.get(Calendar.YEAR),
        date.get(Calendar.MONTH),
        date.get(Calendar.DAY_OF_MONTH)
    )
    dialog.setButton(
        DatePickerDialog.BUTTON_POSITIVE,
        getString(R.string.ok)
    ) { d, p ->
        dialog.onClick(d, p)
        d.cancel()
    }
    dialog.setButton(
        DatePickerDialog.BUTTON_NEGATIVE,
        getString(R.string.cancel)
    ) { d, _ ->
        d.cancel()
    }
    return dialog
}

fun Context.showTimePicker(
    date: Calendar,
    listener: TimePickerDialog.OnTimeSetListener
): TimePickerDialog {
    return TimePickerDialog(
        this, listener,
        date.get(Calendar.HOUR_OF_DAY),
        date.get(Calendar.MINUTE), true
    )
}

fun TextView.clickableSpan(
    text: String,
    @ColorInt highLightColor: Int = colorFromTheme(R.attr.colorAccent),
    vararg clickableSpan: Pair<Pair<Int, Int>, ClickableSpan>
) {
    val span = SpannableString(text)
    clickableSpan.forEach {
        span.setSpan(it.second, it.first.first, it.first.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        span.setSpan(
            ForegroundColorSpan(highLightColor),
            it.first.first,
            it.first.second,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
    setText(span)
    movementMethod = LinkMovementMethod.getInstance()
}

@ColorInt
fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
    val alpha = (Color.alpha(color) * factor).roundToInt()
    val red: Int = Color.red(color)
    val green: Int = Color.green(color)
    val blue: Int = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}

fun Context.getDrawableTinted(@DrawableRes resourceId: Int, @ColorInt tintColor: Int): Drawable {
    val arrow = ContextCompat.getDrawable(this, resourceId)!!
    val wrappedDrawable = DrawableCompat.wrap(arrow).mutate()
    DrawableCompat.setTint(wrappedDrawable, tintColor)
    return wrappedDrawable
}

fun NestedScrollView.onLoadMore(onLoadMore: () -> Unit) {
    setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
        val child = v.getChildAt(v.childCount - 1)
        if (child != null) {
            Timber.tag("___")
                .d("height = ${child.measuredHeight - v.measuredHeight}, old = $oldScrollY, new = $scrollY")
            if (scrollY >= child.measuredHeight - v.measuredHeight && scrollY >= oldScrollY) {
                // code to fetch more data for endless scrolling
                onLoadMore()
            }
        }
    })
}

fun ConstraintSet.setVisibility(
    @IdRes id: Int,
    isVisible: Boolean
) {
    setVisibility(id, if (isVisible) View.VISIBLE else View.GONE)
}

