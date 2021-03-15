package namba.wallet.nambaone.uikit.camera.views.frames

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.size.Size
import kotlin.math.absoluteValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.camera.CameraUtils
import namba.wallet.nambaone.uikit.camera.options.CameraOptions
import namba.wallet.nambaone.uikit.camera.options.CameraSizeSelector
import namba.wallet.nambaone.uikit.camera.options.CropOptions
import namba.wallet.nambaone.uikit.camera.ui.OnScreenOrientationListener
import namba.wallet.nambaone.uikit.camera.ui.ScreenSizeHelper
import namba.wallet.nambaone.uikit.camera.views.CameraView
import namba.wallet.nambaone.uikit.camera.views.RecordButtonView
import timber.log.Timber

/**
 * Start a timer with a given delay.
 *
 * Camera on real devices starts recording with a slight delay (0.5-2 secs), and there are no callbacks provided
 * by the system for the start a media recorder. Since there is no a real way to detect, it is recommended
 * to add a delay.
 * @see https://github.com/natario1/CameraView/issues/156
 * @see https://github.com/natario1/CameraView/issues/65
 */

@SuppressWarnings("TooManyFunctions", "LargeClass")
abstract class BaseCaptureFrame @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val cropOptions: CropOptions = CropOptions()
    protected var isCapturingPicture = false
    protected lateinit var cameraView: CameraView
    protected abstract var cameraOptions: CameraOptions
    private var onMediaReady: ((mediaUri: Uri) -> Unit)? = null
    private var onMediaError: ((e: Throwable) -> Unit)? = null
    private var screenSizeHelper = ScreenSizeHelper()
    protected var recButtonView: RecordButtonView? = null
    protected var progressBar: ProgressBar? = null

    fun setupCameraView(
        cameraView: CameraView,
        cameraOptions: CameraOptions,
        onMediaReady: ((mediaUri: Uri) -> Unit)? = null,
        onMediaError: ((e: Throwable) -> Unit)? = null
    ) {
        this.cameraView = cameraView
        this.onMediaReady = onMediaReady
        this.onMediaError = onMediaError
        calculateScreenSize(context.resources.configuration)
        setupCameraViewListener()
        setupCameraOptions(cameraOptions)
    }

    fun calculateScreenSize(newConfig: Configuration?) {
        screenSizeHelper.update(context, newConfig)
    }

    fun setCropOptions(cropOptions: CropOptions) {
        this.cropOptions.startX = cropOptions.startX
        this.cropOptions.startY = cropOptions.startY
        this.cropOptions.width = cropOptions.width
        this.cropOptions.height = cropOptions.height
    }

    private fun setupCameraViewListener() {
        cameraView.getCamera().addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                this@BaseCaptureFrame.onPictureTaken(result.data)
            }

            override fun onCameraError(exception: CameraException) {
                Timber.e(exception, "Camera error")
                onStopCapturingPhoto()
                showControls()
                this@BaseCaptureFrame.onCameraError(exception)
            }

            override fun onCameraOpened(options: com.otaliastudios.cameraview.CameraOptions) {
                showControls()
                setupCameraOptions(options)
            }

            /**
             * [CameraListener] has more methods that can be implemented
             */
        })

        cameraView.getCamera().addFrameProcessor(::processFrame)
    }

    protected fun hideRecordButton() {
        recButtonView?.isVisible = false
    }

    private fun scanFile(mediaUri: Uri) {
        if (mediaUri.path != null) {
            CameraUtils.scanFile(context, mediaUri.path!!) { contentUri ->
                post {
                    if (contentUri != null) {
                        onMediaReady?.invoke(contentUri)
                        onMediaFinish(false)
                    } else {
                        onMediaError?.invoke(RuntimeException("Content uri is null"))
                        onMediaFinish(false)
                    }
                }
            }
        } else {
            onMediaError?.invoke(RuntimeException("Path is null"))
            onMediaFinish(false)
        }
    }

    private fun onMediaFinish(isCameraActive: Boolean) {
        if (isCameraActive) {
            hideProgressBar()
        }
        unlockScreenOrientation()
    }

    private fun hideProgressBar() {
        cameraView.getCamera().isInvisible = false
        progressBar?.isVisible = false
    }

    @SuppressLint("CheckResult")
    private fun onPictureTaken(jpeg: ByteArray) {
        onStopCapturingPhoto()

        val file = CameraUtils.getCaptureFile(context)
        if (file == null) {
            onMediaError?.invoke(RuntimeException("File is null"))
            onMediaFinish(true)
            showControls()
            return
        }
        GlobalScope.launch {
            try {
                val f = if (cropOptions.width <= 0 || cropOptions.height <= 0) {
                    CameraUtils.writeImage(file, jpeg)
                } else {
                    val bitmap = com.otaliastudios.cameraview.CameraUtils.decodeBitmap(jpeg)
                    if (bitmap != null) {
                        CameraUtils.cropBitmap(bitmap, cropOptions, width, height, file)
                    } else {
                        throw IllegalStateException("Unable to decode file")
                    }
                }

                scanFile(Uri.fromFile(f))
            } catch (e: Throwable) {
                onMediaError?.invoke(e)
                onMediaFinish(true)
                showControls()
            }
        }
    }

    private fun onStartCapturingPhoto() {
        isCapturingPicture = true
        lockScreenOrientation()
    }

    private fun onStopCapturingPhoto() {
        isCapturingPicture = false
    }

    private fun lockScreenOrientation() {
        (context as? OnScreenOrientationListener)?.lockScreenOrientation()
    }

    private fun unlockScreenOrientation() {
        (context as? OnScreenOrientationListener)?.unlockScreenOrientation()
    }

    private fun chooseOptimalSize(choices: List<Size>, size: CameraSizeSelector, aspectRatio: AspectRatio): Size? {
        val bigEnough =
            choices.filter {
                it.height == it.width * aspectRatio.height / aspectRatio.width &&
                    it.width >= size.maxWidth &&
                    it.height >= size.maxHeight
            }

        return if (bigEnough.isNotEmpty()) {
            bigEnough.min()
        } else {
            choices.max()
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun filterAvailableSizes(availableSizes: List<Size>, size: CameraSizeSelector): List<Size> {
        val aspectRatio = if ((screenSizeHelper.getScreenRatio() - ScreenSizeHelper.SIZE_4x3).absoluteValue < 0.1f) {
            AspectRatio(3, 4)
        } else {
            AspectRatio(9, 16)
        }

        val filteredByDimensionSizes = availableSizes
            .filter { it.width <= size.maxWidth && it.height <= size.maxHeight }
            .sortedByDescending { it.width * it.height }

        val optimalSize = chooseOptimalSize(filteredByDimensionSizes, size, aspectRatio)

        return if (optimalSize != null) {
            arrayListOf(optimalSize)
        } else {
            filteredByDimensionSizes
        }
    }

    /**
     * Sets frame options.
     * Can be used to show/hide control buttons based on [CameraOptions].
     */
    protected open fun setupCameraOptions(cameraOptions: CameraOptions) {
        setPhotoSize(cameraOptions.photoSize)
        setSnapshotSize(cameraOptions.snapshotSize)
        setVideoSize(cameraOptions.videoSize)

        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
        cameraView.getCamera().playSounds = ringerMode == AudioManager.RINGER_MODE_NORMAL
        cameraView.getCamera().audio = Audio.OFF
        cameraView.getCamera().facing = cameraOptions.facing
    }

    private fun setPhotoSize(sizeSelector: CameraSizeSelector) {
        cameraView.getCamera().setPictureSize {
            filterAvailableSizes(it, sizeSelector)
        }
    }

    private fun setSnapshotSize(sizeSelector: CameraSizeSelector) {
        val camera = cameraView.getCamera()
        camera.setSnapshotMaxWidth(sizeSelector.maxWidth)
        camera.setSnapshotMaxHeight(sizeSelector.maxHeight)
    }

    private fun setVideoSize(sizeSelector: CameraSizeSelector) {
        cameraView.getCamera().setVideoSize {
            filterAvailableSizes(it, sizeSelector)
        }
    }

    /**
     * Sets camera options.
     * Can be used to show/hide control buttons based on [CameraOptions].
     */
    protected abstract fun setupCameraOptions(options: com.otaliastudios.cameraview.CameraOptions?)

    /**
     * Show control buttons
     */
    protected abstract fun showControls()

    /**
     * Hide control buttons
     */
    protected abstract fun hideControls()

    protected open fun processFrame(frame: Frame) {}

    /**
     * Takes a photo
     */
    protected fun capturePhoto() {
        if (isCapturingPicture) return

        onStartCapturingPhoto()

        cameraView.getCamera().mode = Mode.PICTURE
        cameraView.getCamera().takePicture()
    }

    /**
     * Quits when there is a camera error, but can be overridden.
     */
    protected open fun onCameraError(exception: CameraException) {
        if (exception.message?.contains("stop failed") != true) {
            context.toast(R.string.error_occurred)
        }
    }

    private data class AspectRatio(val width: Int, val height: Int)
}
