package namba.wallet.nambaone.common.locale

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

private const val LANGUAGE_KEY = "LANGUAGE"

class LocaleInteractor(
    private val sharedPreferences: SharedPreferences
) {
    var customLocale: Locale? = sharedPreferences.getString(LANGUAGE_KEY, null)?.let { Locale(it) }
        set(value) {
            sharedPreferences.edit(commit = true) { putString(LANGUAGE_KEY, value?.language) }
            field = value
        }

    private val systemLocale: Locale
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }

    private val currentLocale: Locale
        get() = customLocale ?: systemLocale

    fun applyLocale(activity: Activity) {
        with(activity.resources) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(currentLocale)
                val localeList = LocaleList(currentLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                updateConfiguration(configuration, displayMetrics)
            } else {
                configuration.setLocale(currentLocale)
                updateConfiguration(configuration, displayMetrics)
            }
        }
    }
}
