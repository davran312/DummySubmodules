package namba.wallet.nambaone.uikit.camera.options

import android.graphics.Bitmap

data class CropOptions(
    var startX: Float = 0f,
    var startY: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f,
    val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    val imageQuality: Int = MAX_IMAGE_QUALITY
) {

    companion object {
        const val MAX_IMAGE_QUALITY = 100
    }
}
