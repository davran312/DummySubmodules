package namba.wallet.nambaone.uikit.picker.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Thumbnail(
    val itemId: Long,
    /**
     * Content uri of a thumbnail, for example:
     *
     * content://media/external/video/thumbnails/1421
     * content://media/external/images/thumbnails/1457
     */
    val uri: Uri,
    /**
     * File path of a thumbnail, for example:
     *
     * /storage/emulated/0/DCIM/.thumbnails/1543123695613.jpg
     */
    val path: String
) : Parcelable
