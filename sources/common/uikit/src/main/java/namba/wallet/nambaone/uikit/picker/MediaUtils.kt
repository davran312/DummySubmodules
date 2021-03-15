package namba.wallet.nambaone.uikit.picker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import namba.wallet.nambaone.uikit.picker.model.Image
import namba.wallet.nambaone.uikit.picker.model.Thumbnail

object MediaUtils {

    private const val DEFAULT_MIME_TYPE = "application/octet-stream"

    private val projectionImages = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.ORIENTATION
    )

    /**
     * Get list of [MediaItem]s from content uri.
     *
     * For example:
     */
    @Suppress("LongMethod", "StringLiteralDuplication")
    fun getPath(context: Context, uris: List<Uri>): List<Image> {
        val images = mutableListOf<Image>()
        val imageIds = mutableListOf<String>()

        uris.forEach { uri ->
            if (uri.toString().contains("/images/")) {
                uri.lastPathSegment?.let { imageIds.add(it) }
            } else {
                val mimeType = FileUtils.getMimeType(context, uri)
                if (mimeType == null || !mimeType.contains("image")) return@forEach
                images.add(
                    Image(
                        dateAdded = getDateAdded(context, uri),
                        uri = uri,
                        path = uri.path.orEmpty(),
                        thumbnail = null,
                        mimeType = mimeType,
                        itemId = uri.hashCode().toLong()
                    )
                )
            }
        }
        val selection = MediaStore.Images.Media._ID + " IN (" + "?,".repeat(imageIds.size).removeSuffix(",") + ")"
        images.addAll(getImages(context, selection, imageIds.toTypedArray()))

        return images.sortedBy { uris.indexOf(it.uri) }
    }

    suspend fun getRecentImages(context: Context): List<Image> = withContext(Dispatchers.IO) {
        getImages(context).sortedWith(compareByDescending { it.dateAdded })
    }

    private fun getDateAdded(context: Context, uri: Uri): Long {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (!it.moveToFirst()) return@use null
            var dateAddedColumn = it.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
            if (dateAddedColumn == -1) dateAddedColumn = it.getColumnIndex("last_modified")
            return if (dateAddedColumn != -1) it.getLong(dateAddedColumn) else 0L
        } ?: return 0L
    }

    private fun getImages(
        context: Context,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): List<Image> {

        val thumbnails = getImageThumbnails(context)

        val cursor = MediaStore.Images.Media.query(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projectionImages,
            selection,
            selectionArgs,
            MediaStore.Images.Media.DATE_ADDED + " DESC")

        val result = ArrayList<Image>()

        cursor.use {
            if (it != null && it.moveToFirst()) {
                val imageIdColumn = it.getColumnIndex(MediaStore.Images.Media._ID)
                val dataColumn = it.getColumnIndex(MediaStore.Images.Media.DATA)
                val dateAddedColumn = it.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                val mimeTypeColumn = it.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE)
                do {
                    val imageId = it.getLong(imageIdColumn)
                    val path = it.getString(dataColumn)
                    val dateAdded = it.getLong(dateAddedColumn)
                    val mimeType = it.getStringOrNull(mimeTypeColumn)
                    val foundThumbnail = thumbnails.firstOrNull { thumbnail -> thumbnail.itemId == imageId }
                    val recentImages = Image(
                        itemId = imageId,
                        uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId.toString()),
                        path = path,
                        dateAdded = dateAdded,
                        mimeType = mimeType ?: DEFAULT_MIME_TYPE,
                        thumbnail = foundThumbnail
                    )
                    result.add(recentImages)
                } while (it.moveToNext())
            }
        }

        return result
    }

    private fun getImageThumbnails(context: Context): List<Thumbnail> {
        val thumbnailCursor = context.contentResolver
            .query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Thumbnails.KIND + "=?",
                arrayOf(MediaStore.Images.Thumbnails.MINI_KIND.toString()),
                null
            )

        val thumbnails = ArrayList<Thumbnail>()

        thumbnailCursor.use {
            if (it != null && it.moveToFirst()) {
                val imageIdColumn = it.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID)
                val thumbnailPathColumn = it.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
                do {
                    val imageId = it.getLong(imageIdColumn)
                    val uri =
                        Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageId.toString())
                    val path = it.getString(thumbnailPathColumn) ?: String()
                    thumbnails.add(Thumbnail(imageId, uri, path))
                } while (it.moveToNext())
            }
        }
        return thumbnails
    }
}
