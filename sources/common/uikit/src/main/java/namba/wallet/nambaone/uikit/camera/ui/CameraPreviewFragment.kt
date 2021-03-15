package namba.wallet.nambaone.uikit.camera.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.CameraUtils
import java.io.File
import kotlinx.android.synthetic.main.fragment_camera_preview.accept
import kotlinx.android.synthetic.main.fragment_camera_preview.decline
import kotlinx.android.synthetic.main.fragment_camera_preview.imageView
import kotlinx.android.synthetic.main.fragment_camera_preview.topPanel
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.common.utils.withArgs
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.picker.FileUtils
import timber.log.Timber

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CameraPreviewFragment : Fragment() {

    companion object {
        const val MEDIA_URI_EXTRA = "MEDIA_URI"

        fun newInstance(mediaUri: Uri) =
            CameraPreviewFragment().withArgs(
                MEDIA_URI_EXTRA to mediaUri
            )
    }

    private val mediaUri: Uri by args(MEDIA_URI_EXTRA, Uri.EMPTY)

    private lateinit var mediaFile: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_camera_preview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaFile = File(FileUtils.getPath(view.context, mediaUri))

        if (!isReadable()) {
            toast(R.string.file_not_available)
            finish()
            return
        }

        topPanel?.isVisible = true
        accept?.setOnClickListener { (activity as? CameraActivity)?.sendResult(mediaUri) }
        decline?.setOnClickListener { finish() }
        loadPhoto()
    }

    private fun isReadable(): Boolean {
        return try {
            mediaFile.exists() && mediaFile.isFile
        } catch (e: SecurityException) {
            Timber.e(e, "Couldn't read file")
            false
        }
    }

    private fun finish() {
        activity?.onBackPressed()
    }

    private fun loadPhoto() {
        imageView.isVisible = true

        val displayWidth = resources.displayMetrics.widthPixels
        val displayHeight = resources.displayMetrics.heightPixels

        CameraUtils.decodeBitmap(mediaFile.readBytes(), displayWidth, displayHeight) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                toast(R.string.file_not_available)
                finish()
            }
        }
    }
}
