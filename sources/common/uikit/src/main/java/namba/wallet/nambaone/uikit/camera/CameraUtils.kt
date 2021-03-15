package namba.wallet.nambaone.uikit.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.otaliastudios.cameraview.controls.Facing
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import namba.wallet.nambaone.uikit.camera.options.CropOptions
import timber.log.Timber

object CameraUtils {

    private const val CAMERA = "Camera"
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US)

    fun hasCameras(context: Context): Boolean =
        com.otaliastudios.cameraview.CameraUtils.hasCameras(context)

    fun hasCameraFacing(context: Context, facing: Facing): Boolean =
        com.otaliastudios.cameraview.CameraUtils.hasCameraFacing(context, facing)

    fun writeImage(file: File, bytes: ByteArray): File {
        val outputStream = FileOutputStream(file)
        bytes.inputStream().use { input ->
            outputStream.use { fileOut ->
                input.copyTo(fileOut)
            }
        }
        return file
    }

    @Suppress("MagicNumber")
    fun cropBitmap(
        bitmap: Bitmap,
        cropOptions: CropOptions,
        screenWidth: Int,
        screenHeight: Int,
        file: File
    ): File {
        val croppedBitmap = if (bitmap.width < bitmap.height) {
            val widthRatio = bitmap.width / screenWidth.toFloat()
            val heightRatio = bitmap.height / screenHeight.toFloat()
            Bitmap.createBitmap(
                bitmap,
                (cropOptions.startX * widthRatio).roundToInt(),
                (cropOptions.startY * heightRatio).roundToInt(),
                (cropOptions.width * widthRatio).roundToInt(),
                (cropOptions.height * heightRatio).roundToInt()
            )
        } else {
            val widthRatio = bitmap.width / screenHeight.toFloat()
            val heightRatio = bitmap.height / screenWidth.toFloat()
            val matrix = Matrix()
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                (cropOptions.startY * heightRatio).roundToInt(),
                (cropOptions.startX * widthRatio).roundToInt(),
                (cropOptions.height * heightRatio).roundToInt(),
                (cropOptions.width * widthRatio).roundToInt(),
                matrix,
                false
            )
        }
        croppedBitmap.compress(cropOptions.compressFormat, cropOptions.imageQuality, FileOutputStream(file))
        croppedBitmap.recycle()
        return file
    }

    private fun getApplicationName(context: Context): String {
        val pm = context.applicationContext.packageManager
        val applicationInfo = try {
            pm.getApplicationInfo(context.applicationContext.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        return if (applicationInfo != null) {
            pm.getApplicationLabel(applicationInfo).toString()
        } else {
            CAMERA
        }
    }

    /**
     * Scan file so it gets populated (saved) in MediaStore.
     */
    fun scanFile(context: Context, filepath: String, onReady: (contentUri: Uri?) -> Unit) {
        MediaScannerConnection.scanFile(context, arrayOf(filepath), null) { _, contentUri: Uri? ->
            onReady.invoke(contentUri)
        }
    }

    /**
     * Store files in external directory, so they can be scanned by the system (MediaScanner).
     *
     * @return - a file to store the captured media
     *
     * file:/storage/emulated/0/Pictures/UIKit/IMG_20190110_122152_704.jpg
     * file:/storage/emulated/0/Pictures/UIKit/VID_20190110_122152_704.mp4
     */
    fun getCaptureFile(context: Context): File? {
        val externalDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            getApplicationName(context)
        )

        val dir = try {
            externalDir.mkdirs()

            if (externalDir.canWrite()) {
                externalDir
            } else {
                Timber.e("Unable to write to directory, path = ${externalDir.absolutePath}")
                return null
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Unable to create directory, path = ${externalDir.absolutePath}")
            return null
        }

        val formattedDate = simpleDateFormat.format(Date())
        val file = File(dir, "IMG_$formattedDate.jpg")

        try {
            if (file.exists()) file.delete()
        } catch (e: SecurityException) {
            Timber.e(e, "Unable to delete file, path = ${file.absolutePath}")
        }

        return file
    }
}
