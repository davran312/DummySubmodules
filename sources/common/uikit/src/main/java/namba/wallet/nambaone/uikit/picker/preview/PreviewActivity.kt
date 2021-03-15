package namba.wallet.nambaone.uikit.picker.preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.set
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_picker_preview.itemCheckbox
import kotlinx.android.synthetic.main.activity_picker_preview.itemCountTextView
import kotlinx.android.synthetic.main.activity_picker_preview.previewViewPager
import kotlinx.android.synthetic.main.activity_picker_preview.sendButton
import kotlinx.android.synthetic.main.activity_picker_preview.toolbar
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.extensions.toast
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.picker.MediaPickerDialog
import namba.wallet.nambaone.uikit.picker.MediaUtils
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.PickerOptions
import timber.log.Timber

private const val MEDIA_PICKER_OPTIONS_EXTRA = "media_picker_options_extra"
private const val SELECTED_MEDIA_ITEM_IDS_EXTRA = "selected_media_item_ids_extra"
private const val PREVIEW_MEDIA_ITEM_ID_EXTRA = "preview_media_item_id_extra"

// todo kill it with the fire
class PreviewActivity : AppCompatActivity() {

    companion object {

        fun createIntent(
            context: Context,
            pickerOptions: PickerOptions,
            selectedMediaItemIds: List<Long>,
            previewItemId: Long?
        ): Intent = Intent(context, PreviewActivity::class.java)
            .putExtra(MEDIA_PICKER_OPTIONS_EXTRA, pickerOptions)
            .putExtra(SELECTED_MEDIA_ITEM_IDS_EXTRA, selectedMediaItemIds.toLongArray())
            .putExtra(PREVIEW_MEDIA_ITEM_ID_EXTRA, previewItemId)
    }

    private val pickerOptions: PickerOptions by args(MEDIA_PICKER_OPTIONS_EXTRA, PickerOptions())
    private val preSelectedItemIds: LongArray by args(SELECTED_MEDIA_ITEM_IDS_EXTRA, longArrayOf())
    private val previewItemId: Long? by args(PREVIEW_MEDIA_ITEM_ID_EXTRA)

    private val images = mutableListOf<Image>()
    private val selectedImages = mutableSetOf<Image>()
    private lateinit var currentImage: Image

    private var previousPosition: Int = -1

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_preview)
        supportActionBar?.hide()
        window.statusBarColor = Color.BLACK

        lifecycleScope.launchWhenStarted {
            try {
                val items = MediaUtils.getRecentImages(this@PreviewActivity)
                if (items.isEmpty()) {
                    toast(R.string.files_not_found)
                    finish()
                } else {
                    setupData(items)
                    setupViews()
                }
            } catch (e: Throwable) {
                Timber.e(e, "Unable to get user media")
            }
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }

        sendButton.isInvisible = true
        sendButton.setOnClickListener {
            if (images.isNotEmpty()) {
                val result = if (selectedImages.isEmpty()) listOf(currentImage) else selectedImages
                val intent = MediaPickerDialog.createResultIntentWithSelected(result.toList())
                setResult(Activity.RESULT_OK, intent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = MediaPickerDialog.createResultIntentWithSelected(selectedImages.toList())
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    private fun setupData(items: List<Image>) {
        images.clear()
        images.addAll(items)
        selectedImages.clear()
        selectedImages.addAll(preSelectedItemIds.map { itemId -> items.first { it.itemId == itemId } })
        currentImage =
            images.firstOrNull { it.itemId == previewItemId } ?: selectedImages.firstOrNull() ?: items.first()
    }

    private var isInitialized = false

    private fun setupViews() {
        previewViewPager.adapter = MediaPagerAdapter(images, supportFragmentManager)
        val currentItemPosition = images.indexOfFirst { currentImage.itemId == it.itemId }
        if (currentItemPosition != -1) {
            previewViewPager.setCurrentItem(currentItemPosition, true)
            currentImage = images[currentItemPosition]
            previousPosition = currentItemPosition
            sendButton.isVisible = true
        }
        previewViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (!isInitialized) {
                    // we need to initialize first page in this way, because onPageSelected will not be called
                    isInitialized = true
                }
            }

            override fun onPageSelected(position: Int) {
                previousPosition = position
                currentImage = images[position]
                updateSelection()
            }
        })

        itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedImages.contains(currentImage)) {
                    if (selectedImages.size < pickerOptions.maxFiles) {
                        selectedImages.add(currentImage)
                    } else {
                        toast(getString(R.string.select_not_more_than_items, pickerOptions.maxFiles))
                    }
                }
            } else {
                selectedImages.remove(currentImage)
            }

            updateSelection()
        }

        val isMultiSelect = pickerOptions.maxFiles > 1
        itemCheckbox.isVisible = isMultiSelect
        itemCountTextView.isVisible = isMultiSelect
        updateSelection()
    }

    private fun updateSelection() {
        val isMultiSelect = pickerOptions.maxFiles > 1
        if (isMultiSelect) {
            itemCheckbox.isChecked = selectedImages.any { it.itemId == currentImage.itemId }
            itemCountTextView.isVisible = selectedImages.isNotEmpty()
            itemCountTextView.text = selectedImages.count().toString()
        }
    }
}

private class MediaPagerAdapter(private val items: List<Image>, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = SparseArray<PreviewFragment>()

    override fun getItem(position: Int): PreviewFragment = PreviewFragment.newInstance(getMediaItem(position))

    override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as PreviewFragment
        fragments[position] = fragment
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, obj)
    }

    fun getMediaItem(position: Int): Image = items[position]
}
