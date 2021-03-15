package namba.wallet.nambaone.common.analytics.tracker

import namba.wallet.nambaone.common.analytics.TrackerContract
import timber.log.Timber

private const val TAG_ANALYTICS = "Analytics"

class TimberTracker : TrackerContract {

    override fun setUserProperty(key: String, value: String) =
        Timber.tag(TAG_ANALYTICS).v("setUserProperty() key [$key], value [$value]")

    override fun setUserPropertyOnce(key: String, value: String) =
        Timber.tag(TAG_ANALYTICS).v("setUserPropertyOnce() key [$key], value [$value]")

    override fun logEvent(event: String, params: Array<out Pair<String, Any>>?) =
        Timber.tag(TAG_ANALYTICS).v("logEvent() event [$event], params [${params?.toMap()}]")

    override fun incrementUserProperty(property: String, byValue: Int) =
        Timber.tag(TAG_ANALYTICS).v("incrementUserProperty() property [$property] by $byValue")

    override fun setUserId(userId: String?) =
        Timber.tag(TAG_ANALYTICS).v("setUserId($userId)")

    override fun appendToArray(property: String, value: Int) =
        Timber.tag(TAG_ANALYTICS).v("appendToArray() property [$property] value $value")
}
