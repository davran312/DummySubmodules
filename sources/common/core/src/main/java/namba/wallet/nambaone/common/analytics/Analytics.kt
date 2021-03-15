package namba.wallet.nambaone.common.analytics

import kotlinx.coroutines.launch
import namba.wallet.nambaone.common.AppCoroutineScope
import namba.wallet.nambaone.common.utils.AndroidUtils
import timber.log.Timber

class Analytics(
    appCoroutineScope: AppCoroutineScope,
    private val trackers: Set<TrackerContract>
) : TrackerContract {

    init {
        appCoroutineScope.launch {
            try {
                setUserProperty(UserProperties.ROOT_ACCESS, AndroidUtils.isRooted())
            } catch (e: Throwable) {
                Timber.e(e, "Unable to get root status")
            }
        }
    }

    override fun logEvent(event: String, params: Array<out Pair<String, Any>>?) {
        trackers.forEach { it.logEvent(event, params) }
    }

    override fun setUserProperty(key: String, value: String) {
        trackers.forEach { it.setUserProperty(key, value) }
    }

    fun setUserProperty(key: String, value: Boolean) {
        setUserProperty(key, if (value) "TRUE" else "FALSE")
    }

    override fun setUserPropertyOnce(key: String, value: String) {
        trackers.forEach { it.setUserPropertyOnce(key, value) }
    }

    override fun incrementUserProperty(property: String, byValue: Int) {
        trackers.forEach { it.incrementUserProperty(property, byValue) }
    }

    override fun setUserId(userId: String?) {
        trackers.forEach { it.setUserId(userId) }
    }

    override fun appendToArray(property: String, value: Int) {
        trackers.forEach { it.appendToArray(property, value) }
    }

    companion object {
        const val EMPTY = "empty"

        const val DIRECT = "direct"
        const val APP_FIRST_START = "app_first_start"
        const val APP_BACKGROUND_START = "app_background_start"
    }
}
