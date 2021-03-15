package namba.wallet.nambaone.uikit.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.withArgs

private val TAG = PermissionsDialog::class.java.simpleName
private const val PERMISSION_REQUEST_CODE = 42

private const val PERMISSIONS_EXTRA = "permissions"
private const val PAYLOAD_EXTRA = "payload"

class PermissionsDialog : DialogFragment() {

    companion object {

        fun isPermissionGranted(fragment: Fragment, permissions: List<String>): Boolean {
            return permissions.all { PermissionsUtil.isGranted(fragment.requireContext(), it) }
        }

        fun requestPermissions(fragment: Fragment, permissions: List<String>, payload: Any? = null) {
            if (isPermissionGranted(fragment, permissions)) {
                val status = permissions.map { it to PermissionStatus.GRANTED }.toMap()
                (fragment as? Callback)?.onPermissionsResult(status, payload)
            } else {
                show(fragment.childFragmentManager, permissions, payload)
            }
        }

        fun requestPermissions(activity: FragmentActivity, permissions: List<String>, payload: Any? = null) {
            val allPermissionsAreGranted = permissions.all { PermissionsUtil.isGranted(activity, it) }
            if (allPermissionsAreGranted) {
                val status = permissions.map { it to PermissionStatus.GRANTED }.toMap()
                (activity as? Callback)?.onPermissionsResult(status, payload)
            } else {
                show(activity.supportFragmentManager, permissions, payload)
            }
        }

        private fun show(fragmentManager: FragmentManager, permissions: List<String>, payload: Any?) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                PermissionsDialog()
                    .withArgs(
                        PERMISSIONS_EXTRA to permissions.toTypedArray(),
                        PAYLOAD_EXTRA to payload
                    )
                    .show(fragmentManager, TAG)
            }
        }
    }

    private val permissions by args<Array<String>>(PERMISSIONS_EXTRA, emptyArray())
    private val payload by args<Any?>(PAYLOAD_EXTRA)

    private var isRationaleCache = HashMap<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        for (permission in permissions) {
            isRationaleCache[permission] = shouldShowRequestPermissionRationale(permission)
        }
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && permissions.isNotEmpty()) {
            val result = mutableMapOf<String, PermissionStatus>()
            permissions.forEachIndexed { index, permission ->
                result[permission] = when {
                    // if user grants permission
                    grantResults[index] == PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
                    // if user choose 'Deny', we can ask permission again
                    shouldShowRequestPermissionRationale(permission) -> PermissionStatus.DENIED
                    // if user choose 'Deny' and check 'Never ask again' just now,
                    // the status still 'DENIED' because we must not show PermissionDeniedDialog
                    isRationaleCache[permission] == true -> PermissionStatus.DENIED
                    // if user choose 'Deny' and check 'Never ask again' some time ago,
                    // we can show PermissionDeniedDialog
                    else -> PermissionStatus.PERMANENTLY_DENIED
                }
            }
            (parentFragment as? Callback)?.onPermissionsResult(result, payload)
            (context as? Callback)?.onPermissionsResult(result, payload)
            dismissAllowingStateLoss()
        }
    }

    interface Callback {
        fun onPermissionsResult(status: Map<String, PermissionStatus>, payload: Any?)
    }
}
