package namba.wallet.nambaone.common.analytics

import android.app.Activity
import android.content.Context
import io.branch.referral.Branch
import io.branch.referral.BranchError
import java.text.SimpleDateFormat
import java.util.*
import kz.btsdigital.aitupay.common.analytics.Params
import org.json.JSONObject
import timber.log.Timber

private const val PLAY_REFERRER_TIMEOUT_MILLIS = 5_000L
private const val BRANCH_RETRY_COUNT = 10
private const val BRANCH_CONNECTION_TIMEOUT_MILLIS = 15_000
private const val TIMBER_TAG = "Branch"

class BranchManager(
    private val analytics: Analytics
) {

    companion object {

        fun initialize(context: Context) {
            // В бранче есть внутренний механизм, который смотрит все активити в манифесте
            // и если там есть auto_deep_link_key, автоматически открывает ее. У нас такого нет, поэтому выключаем
            Branch.disableInstantDeepLinking(true)

            Branch.setPlayStoreReferrerCheckTimeout(PLAY_REFERRER_TIMEOUT_MILLIS)
            Branch.getAutoInstance(context).apply {
                // RetryCount работает только для Timeout ошибок, для POOR_NETWORK не срабатывает
                setRetryCount(BRANCH_RETRY_COUNT)
                setNetworkTimeout(BRANCH_CONNECTION_TIMEOUT_MILLIS)
            }
        }
    }

    fun onMainActivityLaunched(activity: Activity, isResumingFromBackground: Boolean) {
        Timber.tag(TIMBER_TAG)
            .e("processAppOpened($activity, isResumingFromBackground=$isResumingFromBackground)")

        val analyticsEntryPoint = if (isResumingFromBackground) {
            Analytics.APP_BACKGROUND_START
        } else {
            Analytics.APP_FIRST_START
        }

        Branch.sessionBuilder(activity)
            .withData(activity.intent.data)
            .withCallback { referringParams: JSONObject?, error: BranchError? ->
                Timber.tag(TIMBER_TAG).d("Processing session init callback... ")
                Timber.tag(TIMBER_TAG).d("referringParams: $referringParams, error: $error")

                if (error == null) {
                    logAcquisitionIfNeeded()
                } else {
                    val exception = BranchException(error.errorCode, error.message)
                    Timber.e(exception, "Error initializing branch")
                    analytics.logEvent(
                        Events.BRANCH_REQUEST,
                        arrayOf(
                            Params.BRANCH_ENTRY_POINT to analyticsEntryPoint,
                            Params.BRANCH_ERROR to exception.toString()
                        )
                    )
                }
            }
            .apply {
                if (isResumingFromBackground) {
                    Timber.tag(TIMBER_TAG)
                        .e("Branch.sessionBuilder($activity).reInit(${activity.intent?.data})")
                    reInit()
                } else {
                    Timber.tag(TIMBER_TAG)
                        .e("Branch.sessionBuilder($activity).init(${activity.intent?.data})")
                    init()
                }
            }
    }

    private fun logAcquisitionIfNeeded() {
        val installBranchParams: JSONObject = Branch.getInstance().firstReferringParams
        val didClickBranchLink = installBranchParams.optBoolean("+clicked_branch_link", false)

        val branchInitResult = if (didClickBranchLink) {
            BranchInitResult(
                channel = installBranchParams.optString("~channel").takeIf { it.isNotBlank() }
                    ?: Analytics.EMPTY,
                campaign = installBranchParams.optString("~campaign").takeIf { it.isNotBlank() }
                    ?: Analytics.EMPTY,
                feature = installBranchParams.optString("~feature").takeIf { it.isNotBlank() }
                    ?: Analytics.EMPTY)
        } else {
            BranchInitResult(
                channel = Analytics.DIRECT,
                campaign = Analytics.DIRECT,
                feature = Analytics.DIRECT
            )
        }

        analytics.setUserPropertyOnce(UserProperties.ACQUISITION_CHANNEL, branchInitResult.channel)
        analytics.setUserPropertyOnce(
            UserProperties.ACQUISITION_CAMPAIGN,
            branchInitResult.campaign
        )
        analytics.setUserPropertyOnce(UserProperties.ACQUISITION_FEATURE, branchInitResult.feature)

        // APP_FIRST_LAUNCH should be logged after acquisition properties set
        analytics.setUserPropertyOnce(
            UserProperties.JOIN_DATE,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(Date())
        )
    }

    private data class BranchInitResult(
        val channel: String,
        val campaign: String,
        val feature: String
    )

    private data class BranchException(val errorCode: Int, override val message: String) :
        Exception()
}
