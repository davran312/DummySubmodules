package namba.wallet.nambaone.uikit.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import kotlinx.android.synthetic.main.dialog_picker.*
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.extensions.attachAdapter
import namba.wallet.nambaone.common.utils.extensions.setThrottleOnClickListener
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.common.utils.withArgs
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.camera.CameraUtils
import namba.wallet.nambaone.uikit.camera.options.CameraOptions
import namba.wallet.nambaone.uikit.camera.ui.CameraActivity
import namba.wallet.nambaone.uikit.permissions.PermissionDeniedDialog
import namba.wallet.nambaone.uikit.permissions.PermissionStatus
import namba.wallet.nambaone.uikit.permissions.PermissionsDialog
import namba.wallet.nambaone.uikit.permissions.PermissionsUtil
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.PickerOptions
import namba.wallet.nambaone.uikit.picker.model.Uri
import namba.wallet.nambaone.uikit.picker.preview.PreviewActivity
import timber.log.Timber

private const val SPAN_COUNT = 2
private const val MEDIA_PICKER_NAME = "media_picker_name"
private const val MEDIA_PICKER_OPTIONS_EXTRA = "media_picker_options"
private const val CAMERA_OPTIONS_EXTRA = "camera_options_extra"

private const val SELECTED_ITEMS_EXTRA = "selected_items_extra"

const val REQUEST_CODE_GALLERY_SELECT = 1
private const val REQUEST_CODE_CAMERA = 2
private const val REQUEST_CODE_PREVIEW = 3

open class MediaPickerDialog : BottomSheetDialogFragment(), MediaAdapter.Callback,
    PermissionsDialog.Callback {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            mediaPickerOptions: PickerOptions? = null,
            cameraOptions: CameraOptions? = null
        ) {
            val dialog = MediaPickerDialog()
                .withArgs(
                    MEDIA_PICKER_OPTIONS_EXTRA to mediaPickerOptions,
                    CAMERA_OPTIONS_EXTRA to cameraOptions
                )
            dialog.show(fragmentManager, MEDIA_PICKER_NAME)
        }

        fun createResultIntentWithSelected(result: List<Image>): Intent =
            Intent().putParcelableArrayListExtra(SELECTED_ITEMS_EXTRA, ArrayList(result))

        fun getSelectedItems(data: Intent?): List<Image> =
            data?.getParcelableArrayListExtra(SELECTED_ITEMS_EXTRA) ?: emptyList()

        fun isRequestCodeGallery(requestCode: Int): Boolean =
            requestCode == REQUEST_CODE_GALLERY_SELECT

        fun startForGalleryResult(fragment: Fragment, galleryIntent: Intent) {
            fragment.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY_SELECT)
        }

        fun startForGalleryResult(activity: Activity, galleryIntent: Intent) {
            activity.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY_SELECT)
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private val pickerOptions: PickerOptions by args(MEDIA_PICKER_OPTIONS_EXTRA, PickerOptions())
    private val cameraOptions: CameraOptions? by args(CAMERA_OPTIONS_EXTRA)
    private var isStoragePermissionsRequested = false

    private val mediaAdapter: MediaAdapter by lazy {
        MediaAdapter(R.layout.item_recent, pickerOptions, this)
            .apply {
                this.setHasStableIds(true)
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dialog_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager =
            GridLayoutManager(context, SPAN_COUNT, RecyclerView.HORIZONTAL, false)
        val itemSpaceWidth = resources.getDimension(R.dimen.media_picker_items_space).toInt()
        val itemWidth = resources.getDimensionPixelSize(R.dimen.media_picker_item_width)

        // Set fixed height, otherwise height will change (jump) while loading media
        recyclerView.layoutParams.apply {
            height = itemWidth * SPAN_COUNT + itemSpaceWidth * (SPAN_COUNT - 1)
        }
        recyclerView.addItemDecoration(MarginItemDecoration(itemSpaceWidth))
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        recyclerView.attachAdapter(mediaAdapter)
        (recyclerView?.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        errorTextView.isVisible = false
        settingsButton.isVisible = false
        counterFab.isVisible = false

        cameraButton.isVisible = pickerOptions.showIconsOptions.showCameraIcon
        galleryButton.isVisible = pickerOptions.showIconsOptions.showPickFromGalleryIcon

        cameraButton.setThrottleOnClickListener { onCameraClick() }
        galleryButton.setThrottleOnClickListener { onGalleryClick() }

        counterFab.setThrottleOnClickListener { sendResult(Uri(mediaAdapter.getSelectedItems())) }
        settingsButton.setThrottleOnClickListener {
            PermissionDeniedDialog.openSettings(
                requireContext()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (isStoragePermissionsRequested) {
            val status = PermissionsUtil.getPermissionStatus(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            onStoragePermissionStatus(status)
        } else {
            isStoragePermissionsRequested = true
            // todo это ооооочень неочевидно, что при этом вызовется loadRecents(), но он вызовется
            PermissionsDialog.requestPermissions(
                this,
                listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_GALLERY_SELECT,
            REQUEST_CODE_PREVIEW -> {
                val selectedItems = data?.getParcelableArrayListExtra<Image>(SELECTED_ITEMS_EXTRA)
                if (resultCode == Activity.RESULT_OK) {
                    sendResult(Uri(selectedItems ?: emptyList()))
                } else {
                    if (pickerOptions.maxFiles > 1) mediaAdapter.setSelectedItems(
                        selectedItems ?: emptyList()
                    )
                }
            }
            REQUEST_CODE_CAMERA -> {
                PermissionsDialog.requestPermissions(
                    this,
                    listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
                if (resultCode == Activity.RESULT_OK) {
                    val selectedItems =
                        data?.getParcelableArrayListExtra<Image>(SELECTED_ITEMS_EXTRA)
                    onMediaPreview(selectedItems ?: emptyList(), null)
                }
            }
        }
    }

    override fun onPermissionsResult(status: Map<String, PermissionStatus>, payload: Any?) {
        onStoragePermissionStatus(status[Manifest.permission.WRITE_EXTERNAL_STORAGE])
        onCameraPermissionStatus(status[Manifest.permission.CAMERA])
    }

    private fun onStoragePermissionStatus(status: PermissionStatus?) {
        when (status) {
            PermissionStatus.GRANTED -> onStoragePermissionGranted()
            PermissionStatus.DENIED -> {
                toast(R.string.storage_permission_not_granted)
                dismissAllowingStateLoss()
            }
            PermissionStatus.PERMANENTLY_DENIED -> onStoragePermissionDenied()
        }
    }

    private fun onCameraPermissionStatus(status: PermissionStatus?) {
        when (status) {
            PermissionStatus.GRANTED ->
                CameraActivity.start(this, REQUEST_CODE_CAMERA, cameraOptions, pickerOptions)
            PermissionStatus.DENIED -> toast(R.string.camera_permission_not_granted)
            PermissionStatus.PERMANENTLY_DENIED ->
                PermissionDeniedDialog.show(this, Manifest.permission.CAMERA)
        }
    }

    private fun onCameraClick() {
        PermissionsDialog.requestPermissions(this, listOf(Manifest.permission.CAMERA))
    }

    private fun onGalleryClick() {
        val galleryIntent = MediaGalleryActivity.createIntent(
            requireContext(),
            pickerOptions,
            mediaAdapter.getSelectedItems()
        )
        startForGalleryResult(this, galleryIntent)
    }

    private fun onStoragePermissionGranted() {
        cameraButton.isEnabled = CameraUtils.hasCameras(requireContext())
        galleryButton.isEnabled = true
        errorTextView.isVisible = false
        settingsButton.isVisible = false
        lifecycleScope.launchWhenResumed { loadRecentMediaItems() }
    }

    private fun onStoragePermissionDenied() {
        cameraButton.isEnabled = false
        galleryButton.isEnabled = false
        errorTextView.setText(R.string.storage_permission_alert_message)
        errorTextView.isVisible = true
        settingsButton.isVisible = true
    }

    private suspend fun loadRecentMediaItems() {
        try {
            val mediaItems = MediaUtils.getRecentImages(requireActivity())
            if (mediaItems.isEmpty()) {
                errorTextView.setText(R.string.files_not_found)
                errorTextView.isVisible = true
            } else {
                errorTextView.isVisible = false
                mediaAdapter.setItems(mediaItems)
            }
        } catch (e: Throwable) {
            errorTextView.setText(R.string.media_loading_error)
            errorTextView.isVisible = true
            Timber.e(e, "Unable to get user media")
        }
    }

    private fun sendResult(result: Uri) {
        (parentFragment as? OnMediaItemSelectedListener)?.onItemsSelected(result)
        (context as? OnMediaItemSelectedListener)?.onItemsSelected(result)
        dismissAllowingStateLoss()
    }

    override fun onMediaListUpdated(selectedItemsCount: Int) {
        counterFab.setBadgeCount(selectedItemsCount)
    }

    override fun onMediaPreview(selectedItems: List<Image>, previewItem: Image?) {
        if (pickerOptions.shouldPreviewPhoto) {
            val intent = PreviewActivity.createIntent(
                requireContext(),
                pickerOptions,
                selectedItems.map { it.itemId },
                previewItem?.itemId
            )
            startActivityForResult(intent, REQUEST_CODE_PREVIEW)
        } else {
            val items =
                if (selectedItems.isEmpty() && previewItem != null) listOf(previewItem) else selectedItems
            sendResult(Uri(items))
        }
    }

    override fun onMaxItemsSelected() {
        toast(
            requireContext().getString(
                R.string.select_not_more_than_items,
                pickerOptions.maxFiles
            )
        )
    }

    interface OnMediaItemSelectedListener {
        fun onItemsSelected(result: Uri)
    }
}
