package namba.wallet.nambaone.uikit.actions

import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import namba.wallet.nambaone.uikit.R

class Action private constructor(
    val icon: Drawable? = null,
    @StringRes val textRes: Int,
    val text: String?,
    @AttrRes val textColorRes: Int,
    val onClickListener: (() -> Unit)?
) {

    companion object {

        fun primary(text: String, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = -1,
            text = text,
            textColorRes = R.attr.colorHighEmphasisOnPrimary,
            onClickListener = onClickListener
        )

        fun primary(@StringRes textRes: Int, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = textRes,
            text = null,
            textColorRes = R.attr.colorHighEmphasisOnPrimary,
            onClickListener = onClickListener
        )

        fun primaryLight(text: String, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = -1,
            text = text,
            textColorRes = R.attr.colorMediumEmphasisOnPrimary,
            onClickListener = onClickListener
        )

        fun primaryLight(@StringRes textRes: Int, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = textRes,
            text = null,
            textColorRes = R.attr.colorMediumEmphasisOnPrimary,
            onClickListener = onClickListener
        )

        fun error(text: String, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = -1,
            text = text,
            textColorRes = R.attr.colorErrorOnPrimary,
            onClickListener = onClickListener
        )

        fun error(@StringRes textRes: Int, icon: Drawable? = null, onClickListener: (() -> Unit)?) = Action(
            icon = icon,
            textRes = textRes,
            text = null,
            textColorRes = R.attr.colorErrorOnPrimary,
            onClickListener = onClickListener
        )
    }
}
