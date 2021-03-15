package namba.wallet.nambaone.uikit.camera.options

import android.os.Parcelable
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CameraOptions(
    val photoSize: CameraSizeSelector = CameraSizeSelector.SIZE_720P,
    val snapshotSize: CameraSizeSelector = CameraSizeSelector.SIZE_720P,
    val videoSize: CameraSizeSelector = CameraSizeSelector.SIZE_720P,
    val videoMaxDurationMillis: Int = 0,
    val facing: Facing = Facing.BACK,
    val shouldShowCloseButton: Boolean = false,
    val shouldShowToggleCameraButton: Boolean = true,
    val shouldShowGalleryButton: Boolean = false,
    val shouldPreviewPhoto: Boolean = false,
    val shouldPreviewVideo: Boolean = false,
    val shouldShowRecordButton: Boolean = true
) : Parcelable
