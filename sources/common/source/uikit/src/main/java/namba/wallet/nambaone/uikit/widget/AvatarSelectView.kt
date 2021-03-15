package namba.wallet.nambaone.uikit.widget

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.widget_avatar_select.view.avatarImageView
import namba.wallet.nambaone.common.utils.extensions.dip
import namba.wallet.nambaone.uikit.R

private const val PADDING_DP = 16

class AvatarSelectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : LinearLayout(context, attrs, defStyleAttrs) {

    init {
        View.inflate(context, R.layout.widget_avatar_select, this)

        orientation = HORIZONTAL
        setPadding(dip(PADDING_DP))

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }

    fun setImageURI(uri: Uri) {
        avatarImageView.loadImage(uri)
    }

    fun setAvatar(avatarUrl: String?) {
        avatarImageView.loadImage(avatarUrl)
    }
}
