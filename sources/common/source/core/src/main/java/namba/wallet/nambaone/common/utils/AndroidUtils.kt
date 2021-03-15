package namba.wallet.nambaone.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import java.io.File
import kotlinx.coroutines.withTimeout

object AndroidUtils {
    private const val CLIPBOARD_LABEL = "CLIPBOARD_LABEL"
    private const val PROCESS_EXECUTE_TIMEOUT_MILLIS = 1000L

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIPBOARD_LABEL, text)
        clipboard.setPrimaryClip(clip)
    }

    suspend fun isRooted(): Boolean {
        // get from build info
        val buildTags = android.os.Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // check if /system/app/Superuser.apk is present
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (ignored: Exception) {
            // ignored
        }

        return withTimeout(PROCESS_EXECUTE_TIMEOUT_MILLIS) {
            canExecuteCommand("/system/xbin/which su") ||
                canExecuteCommand("/system/bin/which su") ||
                canExecuteCommand("which su")
        }
    }

    private fun canExecuteCommand(command: String): Boolean {
        var process: Process? = null
        var result: Boolean

        try {
            process = Runtime.getRuntime().exec(command)
            val code = process?.waitFor()
            result = code == 0
        } catch (e: Exception) {
            result = false
        } finally {
            process?.destroy()
        }
        return result
    }

    fun isMainThread() = Thread.currentThread() === Looper.getMainLooper().thread
}
