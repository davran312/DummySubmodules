package namba.wallet.nambaone.uikit.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_media_gallery.counterFab
import kotlinx.android.synthetic.main.activity_media_gallery.galleryMessage
import kotlinx.android.synthetic.main.activity_media_gallery.galleryRecyclerView
import kotlinx.android.synthetic.main.activity_media_gallery.galleryToolbar
import kotlinx.android.synthetic.main.activity_media_gallery.openGalleryImageButton
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.extensions.attachAdapter
import namba.wallet.nambaone.common.utils.extensions.setThrottleOnClickListener
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.PickerOptions
import namba.wallet.nambaone.uikit.picker.preview.PreviewActivity
import timber.log.Timber

private const val GALLERY_PORTRAIT_SPAN_COUNT = 3
private const val GALLERY_LANDSCAPE_SPAN_COUNT = 5
private const val REQUEST_CODE_PREVIEW = 1
private const val REQUEST_CODE_SELECT_FROM_GALLERY = 2

private const val MEDIA_PICKER_OPTIONS_EXTRA = "media_picker_options_extra"
private const val SELECTED_ITEMS_EXTRA = "selected_items_extra"

class MediaGalleryActivity : AppCompatActivity(), MediaAdapter.Callback {

    companion object {
        fun createIntent(
            context: Context,
            pickerOptions: PickerOptions,
            selectedItems: List<Image> = emptyList()
        ): Intent =
            Intent(context, MediaGalleryActivity::class.java)
                .putExtra(SELECTED_ITEMS_EXTRA, ArrayList(selectedItems))
                .putExtra(MEDIA_PICKER_OPTIONS_EXTRA, pickerOptions)

        fun startForResult(
            activity: Activity,
            pickerOptions: PickerOptions,
            selectedItems: List<Image> = emptyList()
        ) {
            MediaPickerDialog.startForGalleryResult(activity, createIntent(activity, pickerOptions, selectedItems))
        }

        @Suppress("unused")
        fun startForResult(
            fragment: Fragment,
            pickerOptions: PickerOptions,
            selectedItems: List<Image> = emptyList()
        ) {
            MediaPickerDialog.startForGalleryResult(
                fragment,
                createIntent(fragment.requireContext(), pickerOptions, selectedItems)
            )
        }
    }

    private val pickerOptions: PickerOptions by args(MEDIA_PICKER_OPTIONS_EXTRA, PickerOptions())
    private val selectedItems: ArrayList<Image> by args(SELECTED_ITEMS_EXTRA, ArrayList())

    private val mediaAdapter: MediaAdapter by lazy {
        MediaAdapter(R.layout.item_media_gallery, pickerOptions, this)
            .apply {
                this.setHasStableIds(true)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_gallery)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (savedInstanceState == null) {
            galleryToolbar.setNavigationOnClickListener { onBackPressed() }
            counterFab.setThrottleOnClickListener { sendResult(mediaAdapter.getSelectedItems()) }
            initTitle()
            initRecyclerView()
            lifecycleScope.launchWhenCreated { loadRecentMediaItems() }
        }
        openGalleryImageButton.setThrottleOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.type = "image/*"
            when (pickerOptions.maxFiles) {
                1 -> intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                else -> intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_image_or_video_from_gallery)),
                REQUEST_CODE_SELECT_FROM_GALLERY
            )
        }
        openGalleryImageButton.isVisible = pickerOptions.showIconsOptions.showPickFromGalleryIcon
    }

    private fun getReadFilePermissions(uri: Uri) {
        try {
            this.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: SecurityException) {
            Timber.d(e, "MediaGalleryActivity, couldn't get read file permission")
        }
    }

    override fun onBackPressed() {
        val result = ArrayList(mediaAdapter.getSelectedItems())
        val intent = MediaPickerDialog.createResultIntentWithSelected(result)
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val position = (galleryRecyclerView.layoutManager as? GridLayoutManager)?.findFirstVisibleItemPosition() ?: 0
        initLayoutManager()
        galleryRecyclerView.scrollToPosition(position)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mediaAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mediaAdapter.onSaveInstanceState(outState)
    }

    @Suppress("NestedBlockDepth")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PREVIEW) {
            val selectedItems = MediaPickerDialog.getSelectedItems(data)
            if (resultCode == Activity.RESULT_OK) {
                sendResult(selectedItems)
            } else {
                if (pickerOptions.maxFiles > 1) mediaAdapter.setSelectedItems(selectedItems)
            }
        } else if (requestCode == REQUEST_CODE_SELECT_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                val selected = data?.data
                if (selected != null) {
                    Timber.d("From outer gallery picked 1 file - $selected")
                    sendResult(MediaUtils.getPath(this, listOf(selected)))
                } else {
                    var uris = mutableListOf<Uri>()
                    val count = data?.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        val item = data?.clipData?.getItemAt(i)?.uri
                        if (item != null) uris.add(item)
                    }
                    if (uris.size > pickerOptions.maxFiles) {
                        uris = uris.subList(0, pickerOptions.maxFiles)
                        toast(getString(R.string.only_max_pick_files_picked, pickerOptions.maxFiles))
                    }
                    uris.forEach { getReadFilePermissions(it) }
                    Timber.d("From outer gallery picked few files - $uris")
                    sendResult(MediaUtils.getPath(this, uris))
                }
            } else if (resultCode != Activity.RESULT_CANCELED) {
                // show error?
                toast(R.string.select_from_gallery_error)
            }
        }
    }

    override fun onMediaListUpdated(selectedItemsCount: Int) {
        updateSelectedCountTitle(selectedItemsCount)
        counterFab.setBadgeCount(selectedItemsCount)
    }

    override fun onMediaPreview(selectedItems: List<Image>, previewItem: Image?) {
        if (pickerOptions.shouldPreviewPhoto) {
            val intent = PreviewActivity.createIntent(
                this,
                pickerOptions,
                selectedMediaItemIds = selectedItems.map { it.itemId },
                previewItemId = previewItem?.itemId
            )
            startActivityForResult(intent, REQUEST_CODE_PREVIEW)
        } else {
            val items = if (selectedItems.isEmpty() && previewItem != null) listOf(previewItem) else selectedItems
            sendResult(items)
        }
    }

    override fun onMaxItemsSelected() {
        toast(getString(R.string.select_not_more_than_items, pickerOptions.maxFiles))
    }

    private fun sendResult(items: List<Image>) {
        val galleryResultIntent = MediaPickerDialog.createResultIntentWithSelected(ArrayList(items))
        setResult(Activity.RESULT_OK, galleryResultIntent)
        finish()
    }

    private fun initTitle() {
        if (pickerOptions.maxFiles == 1) {
            galleryToolbar.setTitle(R.string.gallery_all_photos)
        } else {
            updateSelectedCountTitle(0)
        }
    }

    private fun updateSelectedCountTitle(selectedCount: Int) {
        galleryToolbar.title = getString(R.string.selected_out_of_total, selectedCount, pickerOptions.maxFiles)
    }

    private fun initRecyclerView() {
        initLayoutManager()

        val itemSpaceWidth = resources.getDimension(R.dimen.media_gallery_items_space).toInt()
        galleryRecyclerView.addItemDecoration(MarginItemDecoration(itemSpaceWidth))

        galleryRecyclerView.setHasFixedSize(true)
        (galleryRecyclerView?.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        galleryRecyclerView.attachAdapter(mediaAdapter)

        counterFab.hide()
    }

    private fun initLayoutManager() {
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GALLERY_LANDSCAPE_SPAN_COUNT
            } else {
                GALLERY_PORTRAIT_SPAN_COUNT
            }

        val gridLayoutManager = GridLayoutManager(this, spanCount, RecyclerView.VERTICAL, false)
        galleryRecyclerView.layoutManager = gridLayoutManager
    }

    private suspend fun loadRecentMediaItems() {
        try {
            val images = MediaUtils.getRecentImages(this@MediaGalleryActivity)
            if (images.isEmpty()) {
                galleryMessage.setText(R.string.files_not_found)
                galleryMessage.isVisible = true
            } else {
                galleryMessage.isVisible = false
                mediaAdapter.setItems(images)
                if (pickerOptions.maxFiles > 1) mediaAdapter.setSelectedItems(selectedItems)
            }
        } catch (e: Throwable) {
            galleryMessage.setText(R.string.media_loading_error)
            galleryMessage.isVisible = true
            Timber.e(e, "Unable to get user media")
        }
    }
}
