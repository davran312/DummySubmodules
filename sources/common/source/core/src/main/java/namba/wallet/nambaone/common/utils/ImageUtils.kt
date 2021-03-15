package namba.wallet.nambaone.common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtils {

    suspend fun resizeImage(file: File, targetWidth: Int, targetHeight: Int): ByteArray = withContext(Dispatchers.IO) {
        val resizedBmp = decodeBitmap(file.absolutePath, targetWidth, targetHeight)

        return@withContext ByteArrayOutputStream().use { outputStream ->
            resizedBmp.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            outputStream.flush()
            resizedBmp.recycle()

            return@use outputStream.toByteArray()
        }
    }

    // https://developer.android.com/topic/performance/graphics/load-bitmap
    private fun decodeBitmap(path: String, targetWidth: Int, targetHeight: Int): Bitmap {
        val options = BitmapFactory.Options()

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, targetWidth, targetHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(path, options)

        return TransformationUtils.rotateImage(bitmap, ExifInterface(path).rotationDegrees)
    }

    private fun calculateInSampleSize(width: Int, height: Int, targetWidth: Int, targetHeight: Int): Int {
        var inSampleSize = 1

        if (width > targetWidth || height > targetHeight) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (
                halfWidth / inSampleSize >= targetWidth &&
                halfHeight / inSampleSize >= targetHeight
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
