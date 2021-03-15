package namba.wallet.nambaone.uikit.camera.views

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.otaliastudios.cameraview.controls.Mode
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.camera.options.CameraOptions
import namba.wallet.nambaone.uikit.camera.options.CropOptions
import namba.wallet.nambaone.uikit.camera.views.frames.BaseCaptureFrame
import namba.wallet.nambaone.uikit.camera.views.frames.SimpleCaptureFrame
import namba.wallet.nambaone.uikit.permissions.PermissionsUtil
import namba.wallet.nambaone.uikit.picker.model.PickerOptions

class CameraView @JvmOverloads constructor(
    @NonNull context: Context,
    @Nullable private val attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), LifecycleObserver {

    private var cameraView: com.otaliastudios.cameraview.CameraView
    var frame: BaseCaptureFrame? = null
    private var pickOptions: PickerOptions? = null

    var lifecycle: Lifecycle? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr, defStyleRes)
        cameraView = com.otaliastudios.cameraview.CameraView(context, attrs)
        cameraView.id = R.id.camera_view_internal
        typedArray.recycle()
    }

    fun getCamera(): com.otaliastudios.cameraview.CameraView = cameraView

    fun canCapturePhoto(): Boolean =
        cameraView.cameraOptions?.supports(Mode.PICTURE) == true

    private fun setupLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycle?.removeObserver(this)
        lifecycle = lifecycleOwner.lifecycle
        lifecycle?.addObserver(this)
        cameraView.setLifecycleOwner(lifecycleOwner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        frame?.isVisible = PermissionsUtil.isGranted(context, Manifest.permission.CAMERA)
    }

    fun setupCamera(
        cameraOptions: CameraOptions,
        lifecycleOwner: LifecycleOwner,
        cropOptions: CropOptions? = null,
        onMediaReady: ((mediaUri: Uri) -> Unit)? = null,
        onMediaError: ((e: Throwable) -> Unit)? = null,
        pickerOptions: PickerOptions,
        frame: SimpleCaptureFrame
    ) {
        if (cameraView.parent == null) {
            addView(cameraView)
        }

        setupLifecycleOwner(lifecycleOwner)

        /**
         * CameraView attributes are propagated down to [com.otaliastudios.cameraview.CameraView]
         */
        pickOptions = pickerOptions
        this.frame = frame
        cropOptions?.let {
            frame.setCropOptions(it)
        }

        frame.setupCameraView(this, cameraOptions, onMediaReady, onMediaError)
    }
}
