package namba.wallet.nambaone.common.utils.extensions

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import java.util.*
import namba.wallet.nambaone.core.R

fun Context.showInfoDialog(
    @StringRes messageRes: Int,
    lifecycleOwner: LifecycleOwner,
    dismissCallback: (() -> Unit)? = null
) {
    AlertDialog.Builder(this)
        .setMessage(messageRes)
        .setPositiveButton(R.string.ok, null)
        .showAutoCancel(lifecycleOwner, dismissCallback)
}

fun Context.showInfoDialog(message: String, lifecycleOwner: LifecycleOwner, dismissCallback: (() -> Unit)? = null) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(R.string.ok, null)
        .showAutoCancel(lifecycleOwner, dismissCallback)
}

fun Context.showInfoDialog(throwable: Throwable? = null, lifecycleOwner: LifecycleOwner) {
    showInfoDialog(throwable.getErrorMessage(this), lifecycleOwner)
}

fun Context.showRetryDialog(
    throwable: Throwable? = null,
    lifecycleOwner: LifecycleOwner,
    retryCallback: (() -> Unit)? = null
) {
    val callback = retryCallback?.let { DialogInterface.OnClickListener { _, _ -> it() } }
    AlertDialog.Builder(this)
        .setMessage(throwable.getErrorMessage(this))
        .setPositiveButton(R.string.dialog_retry_button, callback)
        .setNegativeButton(R.string.cancel, null)
        .showAutoCancel(lifecycleOwner)
}

fun Context.showRetryDialog(
    messageRes: Int,
    lifecycleOwner: LifecycleOwner,
    retryCallback: (() -> Unit)?
) {
    val callback = retryCallback?.let { DialogInterface.OnClickListener { _, _ -> it() } }
    AlertDialog.Builder(this)
        .setMessage(messageRes)
        .setPositiveButton(R.string.dialog_retry_button, callback)
        .setNegativeButton(R.string.cancel, null)
        .showAutoCancel(lifecycleOwner)
}

fun Fragment.showInfoDialog(@StringRes messageRes: Int, dismissCallback: (() -> Unit)? = null) {
    requireContext().showInfoDialog(messageRes, viewLifecycleOwner, dismissCallback)
}

fun Fragment.showInfoDialog(message: String, dismissCallback: (() -> Unit)? = null) {
    requireContext().showInfoDialog(message, viewLifecycleOwner, dismissCallback)
}

fun Fragment.showInfoDialog(throwable: Throwable? = null, dismissCallback: (() -> Unit)? = null) {
    showInfoDialog(throwable.getErrorMessage(requireContext()), dismissCallback)
}

fun Fragment.showRetryDialog(throwable: Throwable? = null, retryCallback: (() -> Unit)? = null) {
    requireContext().showRetryDialog(throwable, viewLifecycleOwner, retryCallback)
}

fun Fragment.showRetryDialog(messageRes: Int, retryCallback: (() -> Unit)?) {
    requireContext().showRetryDialog(messageRes, viewLifecycleOwner, retryCallback)
}

fun AlertDialog.Builder.showAutoCancel(
    lifecycleOwner: LifecycleOwner?,
    dismissCallback: (() -> Unit)? = null
) {
    if (lifecycleOwner == null) {
        show()
    } else {
        DialogLifecycleObserver(lifecycleOwner, show(), dismissCallback)
    }
}
