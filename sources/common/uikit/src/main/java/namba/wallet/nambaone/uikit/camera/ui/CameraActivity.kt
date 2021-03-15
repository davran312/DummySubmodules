package namba.wallet.nambaone.uikit.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_camera.*
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.camera.options.CameraOptions
import namba.wallet.nambaone.uikit.permissions.PermissionDeniedDialog
import namba.wallet.nambaone.uikit.permissions.PermissionStatus
import namba.wallet.nambaone.uikit.permissions.PermissionsDialog
import namba.wallet.nambaone.uikit.picker.MediaPickerDialog
import namba.wallet.nambaone.uikit.picker.MediaUtils
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.PickerOptions
import timber.log.Timber

private const val CAMERA_OPTIONS_EXTRA = "camera_options_extra"
private const val PICKER_OPTIONS_EXTRA = "picker_options_extra"

class CameraActivity :
    AppCompatActivity(),
    OnMediaReadyListener,
    PermissionsDialog.Callback,
    PermissionDeniedDialog.Callback,
    OnScreenOrientationListener {

    companion object {

        fun start(
            from: Fragment,
            requestCode: Int,
            cameraOptions: CameraOptions? = null,
            pickerOptions: PickerOptions? = null
        ) {
            val cameraIntent = createIntent(from.requireContext(), cameraOptions, pickerOptions)
            from.startActivityForResult(cameraIntent, requestCode)
        }

        fun start(
            from: AppCompatActivity,
            requestCode: Int,
            cameraOptions: CameraOptions? = null,
            pickerOptions: PickerOptions? = null
        ) {
            val cameraIntent = createIntent(from, cameraOptions, pickerOptions)
            from.startActivityForResult(cameraIntent, requestCode)
        }

        fun extractMedia(data: Intent?): Image? =
            MediaPickerDialog.getSelectedItems(data).firstOrNull()

        private fun createIntent(
            context: Context,
            cameraOptions: CameraOptions?,
            pickerOptions: PickerOptions?
        ) = Intent(context, CameraActivity::class.java)
            .putExtra(CAMERA_OPTIONS_EXTRA, cameraOptions)
            .putExtra(PICKER_OPTIONS_EXTRA, pickerOptions)
    }

    private val cameraOptions by args(CAMERA_OPTIONS_EXTRA, CameraOptions())
    private val pickerOptions by args(PICKER_OPTIONS_EXTRA, PickerOptions())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions =
            mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        PermissionsDialog.requestPermissions(this, permissions)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        uikitCameraView.frame?.calculateScreenSize(newConfig)
    }

    /**
     * Pass results to the fragment manually because right now a [com.otaliastudios.cameraview.CameraView]
     * doesn't send the results directly to the fragment itself.
     * Submitted a bug https://github.com/natario1/CameraView/issues/322
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var cameraGranted = false

        for (i in permissions.indices) {
            if (permissions[i] == Manifest.permission.CAMERA && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                cameraGranted = true
            }
        }

        if (cameraGranted && !uikitCameraView.getCamera().isOpened) {
            uikitCameraView.getCamera().open()
        }
    }

    override fun onPermissionsResult(status: Map<String, PermissionStatus>, payload: Any?) {
        if (status.all { it.value == PermissionStatus.GRANTED }) {
            showCaptureFragment()
            return
        }

        val firstDenied = status.entries.firstOrNull { it.value == PermissionStatus.DENIED }
        if (firstDenied != null) {
            when (firstDenied.key) {
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> toast(R.string.storage_permission_not_granted)
                Manifest.permission.CAMERA -> toast(R.string.camera_permission_not_granted)
            }
            finish()
            return
        }

        val firstPermanentlyDenied =
            status.entries.firstOrNull { it.value == PermissionStatus.PERMANENTLY_DENIED }
        if (firstPermanentlyDenied != null) {
            PermissionDeniedDialog.show(
                activity = this,
                permission = firstPermanentlyDenied.key
            )
        }
    }

    override fun onPermissionDeniedDismiss(permission: String) {
        finish()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // hide system UI
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result from 'MediaGalleryFragment' that was launched from 'Browse Gallery' button on CameraFragment
        if (MediaPickerDialog.isRequestCodeGallery(requestCode) && resultCode == Activity.RESULT_OK) {
            val selectedItems = MediaPickerDialog.getSelectedItems(data)
            if (!selectedItems.isNullOrEmpty()) {
                onMediaReady(selectedItems.first().uri)
            }
        }
    }

    override fun onMediaReady(mediaUri: Uri) {
        if (cameraOptions.shouldPreviewPhoto && !pickerOptions.shouldPreviewPhoto) {
            showPreviewFragment(mediaUri)
        } else {
            sendResult(mediaUri)
        }
    }

    fun sendResult(media: Uri) {
        val items = MediaUtils.getPath(this, listOf(media))
        val intent = MediaPickerDialog.createResultIntentWithSelected(items)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showCaptureFragment() {
        uikitCameraView.setupCamera(
            cameraOptions,
            lifecycleOwner = this,
            onMediaReady = { mediaUri ->
                onMediaReady(mediaUri)
            },
            onMediaError = {
                Timber.e(it, "Media Error")
                if (it.message?.contains("stop failed") != true) {
                    runOnUiThread {
                        toast(R.string.error_occurred)
                    }
                }
            },
            pickerOptions = pickerOptions,
            frame = actionButtons.apply { this.pickerOptions = pickerOptions }
        )
    }

    private fun showPreviewFragment(mediaUri: Uri) {
        replaceScreen(CameraPreviewFragment.newInstance(mediaUri), true)
    }

    private fun replaceScreen(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .apply { if (addToBackStack) addToBackStack(null) }
            .commit()
    }

    override fun lockScreenOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun unlockScreenOrientation() {
        requestedOrientation = resources.configuration.orientation
    }
}

interface OnMediaReadyListener {
    fun onMediaReady(mediaUri: Uri)
}

interface OnScreenOrientationListener {
    fun lockScreenOrientation()
    fun unlockScreenOrientation()
}
