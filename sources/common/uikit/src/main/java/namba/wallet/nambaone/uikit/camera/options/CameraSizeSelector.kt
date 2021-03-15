package namba.wallet.nambaone.uikit.camera.options

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Can control the actual size of the output picture, among the list of available sizes.
 * It is the size of the final picture.
 *
 * For example, let's say camera can shoot pictures in these 5 different sizes:
 *      1. 200x300
 *      2. 400x600
 *      3. 800x1000
 *      4. 1000x1400
 *      5. 3000x4000
 *
 * If you pass CameraSizeSelector(1200, 1200) then it will try to find the most biggest match
 * which is 800x1000. If such size is not found (criteria is not satisfied) it will take the
 * most biggest size that camera supports.
 */
@Parcelize
data class CameraSizeSelector(
    val maxWidth: Int,
    val maxHeight: Int
) : Parcelable {

    companion object {
        /**
         * Use 9/16 ratio when setting width and height
         */
        val SIZE_480P = CameraSizeSelector(480, 720)
        val SIZE_720P = CameraSizeSelector(720, 1280)
        val SIZE_1080P = CameraSizeSelector(1080, 1920)
        val SIZE_2160P = CameraSizeSelector(2160, 3840) // 4K
    }
}
