package namba.wallet.nambaone.uikit.picker.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Image(
    val itemId: Long,
    val uri: Uri,
    val path: String,
    val dateAdded: Long,
    val mimeType: String,
    val thumbnail: Thumbnail?
) : Parcelable
