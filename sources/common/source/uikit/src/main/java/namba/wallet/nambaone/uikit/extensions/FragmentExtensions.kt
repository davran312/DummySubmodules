package namba.wallet.nambaone.uikit.extensions

import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import namba.wallet.nambaone.core.R

fun Fragment.showUrlInCustomTabs(url: String) {
    val uri = Uri.parse(url)
    val customTabsIntent = CustomTabsIntent.Builder()
        .setToolbarColor(colorFromTheme(R.attr.colorSecondary))
        .setShowTitle(true)
        .build()
    customTabsIntent.launchUrl(requireContext(), uri)
}

fun AppCompatActivity.showUrlInCustomTabs(url: String) {
    val uri = Uri.parse(url)
    val customTabsIntent = CustomTabsIntent.Builder()
        .setToolbarColor(colorFromTheme(R.attr.colorSecondary))
        .setShowTitle(true)
        .build()
    customTabsIntent.launchUrl(this, uri)
}

fun Fragment.shareFile(file: File, fileType: String) {
    requireActivity().shareFile(file, fileType)
}

fun Fragment.getTintedDrawable(@DrawableRes drawableRes: Int, @ColorInt tintColor: Int) =
    requireContext().getTintedDrawable(drawableRes, tintColor)


inline  fun <reified T: BottomSheetDialogFragment>FragmentManager.dismiss() {
    val dialogFragment = findFragmentByTag(T::class.java.name) as? BottomSheetDialogFragment
    dialogFragment?.dismiss()
}