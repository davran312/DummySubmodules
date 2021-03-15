package namba.wallet.nambaone.common.analytics.tracker

import android.content.Context
import io.branch.referral.Branch
import namba.wallet.nambaone.common.analytics.TrackerContract
import namba.wallet.nambaone.core.BuildConfig

class BranchTracker(private val context: Context) : TrackerContract {

    init {
        if (BuildConfig.DEBUG) Branch.enableLogging()
    }

    override fun setUserId(userId: String?) {
        if (userId != null) Branch.getInstance(context).setIdentity(userId)
    }

    override fun setUserProperty(key: String, value: String) = Unit
    override fun logEvent(event: String, params: Array<out Pair<String, Any>>?) = Unit
    override fun incrementUserProperty(property: String, byValue: Int) = Unit
    override fun appendToArray(property: String, value: Int) = Unit
    override fun setUserPropertyOnce(key: String, value: String) = Unit
}
