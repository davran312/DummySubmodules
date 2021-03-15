package namba.wallet.nambaone.common.analytics.tracker

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import java.lang.ref.WeakReference
import namba.wallet.nambaone.common.analytics.TrackerContract

class FirebaseTracker(context: Context) : TrackerContract {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val activityLifecycleHandler = ActivityLifecycleHandler()

    init {
        (context as Application).registerActivityLifecycleCallbacks(activityLifecycleHandler)
    }

    override fun setUserProperty(key: String, value: String) {
        firebaseAnalytics.setUserProperty(key, value)
    }

    override fun logEvent(event: String, params: Array<out Pair<String, Any>>?) {
        firebaseAnalytics.logEvent(event, params?.let { bundleOf(*it) })
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }
}

private class ActivityLifecycleHandler : Application.ActivityLifecycleCallbacks {

    private var currentActivityReference: WeakReference<Activity>? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {}
}
