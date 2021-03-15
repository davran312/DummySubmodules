package namba.wallet.nambaone.common.theme

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

private const val NIGHT_MODE_KEY = "NIGHT_MODE_KEY"

/**
 * Separate this one from profile to optimize startup time.
 */
class SettingsInteractor(
    private val sharedPreferences: SharedPreferences
) {

    fun applyCurrentNightMode() {
        AppCompatDelegate.setDefaultNightMode(getNightMode())
    }

    fun setNightMode(@AppCompatDelegate.NightMode mode: Int) {
        sharedPreferences.edit(commit = true) { putInt(NIGHT_MODE_KEY, mode) }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    @AppCompatDelegate.NightMode
    fun getNightMode() = AppCompatDelegate.MODE_NIGHT_NO
        // sharedPreferences.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}
