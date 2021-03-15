package namba.wallet.nambaone.uikit.picker.preview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_picker_preview.imagePhotoView
import namba.wallet.nambaone.common.ui.mvp.BaseFragment
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.withArgs
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.picker.model.Image

private const val MEDIA_ITEM_EXTRA = "media_item_extra"

class PreviewFragment : BaseFragment(R.layout.fragment_picker_preview) {

    companion object {
        fun newInstance(mediaItem: Image): PreviewFragment =
            PreviewFragment().withArgs(MEDIA_ITEM_EXTRA to mediaItem)
    }

    private val mediaItem: Image by args(MEDIA_ITEM_EXTRA)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imagePhotoView.isVisible = true
        Glide.with(this)
            .load(mediaItem.uri)
            .into(imagePhotoView)
    }
}
