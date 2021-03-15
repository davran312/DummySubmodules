package namba.wallet.nambaone.uikit.picker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PickerOptions(
    val maxFiles: Int = 1,
    val shouldPreviewPhoto: Boolean = true,
    val showIconsOptions: ShowIconsOptions = ShowIconsOptions()
) : Parcelable

@Parcelize
data class ShowIconsOptions(
    val showCameraIcon: Boolean = true,
    val showPickFromGalleryIcon: Boolean = false
) : Parcelable
