package namba.wallet.nambaone.uikit.permissions

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

@TargetApi(Build.VERSION_CODES.M)
object PermissionsUtil {

    fun isGranted(context: Context, permission: String): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED

    fun getPermissionStatus(activity: Activity, permission: String): PermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return PermissionStatus.GRANTED
        }

        return when {
            activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
            activity.shouldShowRequestPermissionRationale(permission) -> PermissionStatus.DENIED
            else -> PermissionStatus.PERMANENTLY_DENIED
        }
    }

    fun getPermissionStatus(fragment: Fragment, permissions: List<String>): Boolean {
        return permissions.any { getPermissionStatus(fragment, it) != PermissionStatus.GRANTED }
    }

    fun getPermissionStatus(fragment: Fragment, permission: String): PermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return PermissionStatus.GRANTED
        }

        return when {
            fragment.requireContext()
                .checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED ->
                PermissionStatus.GRANTED

            fragment.shouldShowRequestPermissionRationale(permission) -> PermissionStatus.DENIED
            else -> PermissionStatus.PERMANENTLY_DENIED
        }
    }
}
