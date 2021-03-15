package namba.wallet.nambaone.uikit.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import namba.wallet.nambaone.core.R
import java.io.File

fun Context.shareFile(file: File, fileType: String) {
    val authority = this.packageName + ".fileprovider"
    val uri = FileProvider.getUriForFile(this, authority, file)

    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = fileType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
}

fun Context.sharePdf(file: File) {
    val newIntent = Intent(Intent.ACTION_SEND)
    newIntent.putExtra(
        Intent.EXTRA_STREAM,
        FileProvider.getUriForFile(this, this.packageName + ".fileprovider", file))
    newIntent.type = "application/pdf"
    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(Intent.createChooser(newIntent, getString(R.string.share)))
}

fun Context.getTintedDrawable(@DrawableRes drawableRes: Int, @ColorInt tintColor: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableRes)?.mutate()?.apply { setTint(tintColor) }

@SuppressLint("MissingPermission")
fun Context.vibrate(duration: Long = 500) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(
            duration,
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        vibrator?.vibrate(effect)
    } else {
        vibrator?.vibrate(duration)
    }
}

fun Activity.setStatusBarTextColor(isLightStatusBarTextColor: Boolean) {
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            if (isLightStatusBarTextColor) {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
}

fun String.fromHtml(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    else
        Html.fromHtml(this).toString()
}

@ColorInt
fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor).toInt()
    val red: Int = Color.red(this)
    val green: Int = Color.green(this)
    val blue: Int = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}