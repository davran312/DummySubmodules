package namba.wallet.nambaone.uikit.camera.views.frames

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleObserver
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.camera_frame_simple_record.view.*
import namba.wallet.nambaone.common.utils.extensions.setThrottleOnClickListener
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.camera.CameraUtils
import namba.wallet.nambaone.uikit.camera.options.CameraOptions
import namba.wallet.nambaone.uikit.picker.model.PickerOptions

class SimpleCaptureFrame @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : BaseCaptureFrame(context, attrs, defStyleAttr, defStyleRes),
    LifecycleObserver {

    override var cameraOptions = CameraOptions()
    var pickerOptions: PickerOptions? = null

    init {
        inflate(context, R.layout.camera_frame_simple_record, this)
        recButtonView = findViewById(R.id.recVideoImageView)
        progressBar = findViewById(R.id.photoProcessingProgressBar)
    }

    private fun setupListeners() {
        recButtonView?.initListeners()
        recButtonView?.gestureListener = {
            capturePhotoIfYouCan()
        }

        closeImageView.setThrottleOnClickListener {
            (context as? Activity)?.finish()
        }
    }

    private fun capturePhotoIfYouCan() {
        if (cameraView.canCapturePhoto()) {
            capturePhoto()
        } else {
            context.toast(R.string.camera_capture_picture_not_supported)
            showControls()
        }
    }

    private fun showCloseButton(show: Boolean) {
        closeImageView.isVisible = show
    }

    private fun showToggleCameraButton(show: Boolean) {
        toggleCameraImageView.apply {
            isVisible = if (show) {
                val hasBackCamera = CameraUtils.hasCameraFacing(context, Facing.BACK)
                val hasFrontCamera = CameraUtils.hasCameraFacing(context, Facing.FRONT)

                hasBackCamera && hasFrontCamera
            } else {
                false
            }

            if (!hasOnClickListeners()) setThrottleOnClickListener { toggleCamera() }
        }
    }

    private fun toggleCamera() {
        if (isCapturingPicture) return

        cameraView.getCamera().toggleFacing()
    }

    override fun hideControls() {
        showCloseButton(false)
        showToggleCameraButton(false)
    }

    override fun showControls() {
        recButtonView?.isVisible = cameraOptions.shouldShowRecordButton

        showCloseButton(!isCapturingPicture && cameraOptions.shouldShowCloseButton)
        showToggleCameraButton(
            !isCapturingPicture && cameraOptions.shouldShowToggleCameraButton
        )
    }

    override fun setupCameraOptions(cameraOptions: CameraOptions) {
        super.setupCameraOptions(cameraOptions)
        this.cameraOptions = cameraOptions
    }

    override fun setupCameraOptions(options: com.otaliastudios.cameraview.CameraOptions?) {
        setupListeners()
        cameraView.lifecycle?.addObserver(this)
    }
}
