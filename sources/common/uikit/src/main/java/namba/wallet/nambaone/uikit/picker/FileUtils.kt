package namba.wallet.nambaone.uikit.picker

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import timber.log.Timber

object FileUtils {

    @Suppress("ReturnCount", "NestedBlockDepth")
    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        } else if (DocumentsContract.isDocumentUri(context, uri)) {
            // Get uri related document id.
            val documentId = DocumentsContract.getDocumentId(uri)

            // Get uri authority.
            val uriAuthority = uri.authority

            if ("com.android.providers.media.documents" == uriAuthority) {
                val idArr = documentId.split(":")
                if (idArr.size == 2) {
                    // First item is document type.
                    val docType = idArr[0]

                    // Second item is document real id.
                    val realDocId = idArr[1]

                    // Get content uri by document type.
                    val mediaContentUri = when (docType) {
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    // Get where clause with real document id.
                    val whereClause = MediaStore.Images.Media._ID + " = " + realDocId

                    return getDataColumn(context, mediaContentUri, whereClause, null)
                }
            } else if ("com.android.providers.downloads.documents" == uriAuthority) {
                // Build download uri.
                val downloadUri = Uri.parse("content://downloads/public_downloads")

                // Append download document id at uri end.
                val downloadUriAppendId = ContentUris.withAppendedId(downloadUri, java.lang.Long.valueOf(documentId))

                return getDataColumn(context, downloadUriAppendId, null, null)
            } else if ("com.android.externalstorage.documents" == uriAuthority) {
                val idArr = documentId.split(":")
                if (idArr.size == 2) {
                    val type = idArr[0]
                    val realDocId = idArr[1]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + realDocId
                    }
                }
            }
        }

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {

        val column = MediaStore.MediaColumns.DATA
        val projection = arrayOf(column)
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(column)
                return it.getString(columnIndex)
            }
        }

        return null
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.getType(uri)
        } else {
            try {
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            } catch (e: Exception) {
                Timber.d(e, "${FileUtils::class.java.canonicalName}.getMimeType(context, uri = $uri)")
                null
            }
        }
    }
}
