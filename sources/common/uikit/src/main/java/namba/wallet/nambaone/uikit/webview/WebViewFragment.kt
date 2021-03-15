package namba.wallet.nambaone.uikit.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_webview.*
import namba.wallet.nambaone.common.network.provider.RootCertificateProvider
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.navigation.popScreen
import namba.wallet.nambaone.common.utils.withArgs
import namba.wallet.nambaone.uikit.BuildConfig
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.colorFromTheme
import namba.wallet.nambaone.uikit.extensions.getTintedDrawable
import namba.wallet.nambaone.uikit.extensions.redispatchWindowInsetsToAllChildren
import org.koin.android.ext.android.inject

const val EXTRA_REDIRECT_URL = "EXTRA_REDIRECT_URL"
const val EXTRA_TARGET_URL = "EXTRA_TARGET_URL"
const val EXTRA_TITLE = "EXTRA_TITLE"
const val EXTRA_FILL_VIEW_PORT = "EXTRA_FILL_VIEW_PORT"
const val EXTRA_SHOULD_DROP = "EXTRA_SHOULD_DROP"
const val NAVIGATION_ICON_EXTRA = "NAVIGATION_ICON_EXTRA"
const val NAVIGATION_TINT_COLOR_EXTRA = "NAVIGATION_TINT_COLOR_EXTRA"

private const val URL_DELIMITER = '?'

open class WebViewFragment : Fragment(R.layout.fragment_webview) {

    companion object {

        fun create(
            targetUrl: String,
            redirectUrl: String? = null,
            @StringRes titleRes: Int = 0,
            @DrawableRes icon: Int = 0,
            @AttrRes color: Int = 0,
            shouldDropParams: Boolean = false,
            fillViewPort: Boolean = false
        ): WebViewFragment = WebViewFragment().withArgs(
            EXTRA_TARGET_URL to targetUrl,
            EXTRA_REDIRECT_URL to redirectUrl,
            EXTRA_TITLE to titleRes,
            EXTRA_SHOULD_DROP to shouldDropParams,
            NAVIGATION_ICON_EXTRA to icon,
            NAVIGATION_TINT_COLOR_EXTRA to color,
            EXTRA_FILL_VIEW_PORT to fillViewPort
        )
    }

    private var listener: OnWebViewListener? = null
    private val targetUrl: String by args(EXTRA_TARGET_URL)
    private val redirectUrl: String? by args(EXTRA_REDIRECT_URL)
    private val titleRes: Int by args(EXTRA_TITLE, 0)
    private val shouldDropParams: Boolean by args(EXTRA_SHOULD_DROP, false)
    private val navigationRes: Int by args(NAVIGATION_ICON_EXTRA, 0)
    private val navigationTintColor: Int by args(NAVIGATION_TINT_COLOR_EXTRA, 0)
    private val isFillViewPortEnabled: Boolean by args(EXTRA_FILL_VIEW_PORT, false)

    private val rootCertificateProvider: RootCertificateProvider by inject()
    private val rootCertEncoded: ByteArray by lazy { rootCertificateProvider.rootCertificate.encoded }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as? OnWebViewListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.redispatchWindowInsetsToAllChildren()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nambaToolBar {
            if (titleRes != 0) setTitle(titleRes)
            when {
                navigationRes != 0 && navigationTintColor != 0 -> {
                    val color = colorFromTheme(navigationTintColor)
                    navigationIcon = getTintedDrawable(navigationRes, color)
                }
                navigationRes != 0 && navigationTintColor == 0 -> {
                    navigationIcon = ContextCompat.getDrawable(requireContext(), navigationRes)
                }
            }
            setNavigationOnClickListener {
                popScreen()
            }
        }

        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressView?.isInvisible = newProgress == 100
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                val formattedUrl = if (shouldDropParams) url.substringBefore(URL_DELIMITER) else url
                if (formattedUrl == redirectUrl) {
                    onRedirectUrlSucceed()
                    return false
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                errorView.isVisible = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                errorView.isVisible = true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                val serverCertificate = error?.certificate
                if (serverCertificate != null && isRootCert(serverCertificate)) {
                    handler?.proceed()
                } else {
                    handler?.cancel()
                }
            }
        }
        errorView.onRefreshClickListener = {
            webView.loadUrl(targetUrl)
        }
        webView.loadUrl(targetUrl)
        applyInsets()
    }

    open fun onRedirectUrlSucceed() {
        listener?.onFinished()
        popScreen()
    }

    private fun applyInsets() {
        webViewContainer.redispatchWindowInsetsToAllChildren()
        nambaToolBar.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
    }

    override fun onDestroyView() {
        webView.webChromeClient = null
        webView.webViewClient = null
        super.onDestroyView()
    }

    private fun isRootCert(serverCert: SslCertificate) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) isRootCertCompat(serverCert)
        else serverCert.x509Certificate?.encoded?.let { rootCertEncoded.contentEquals(it) } ?: false

    private fun isRootCertCompat(serverCert: SslCertificate): Boolean {
        val serverCertBundle = SslCertificate.saveState(serverCert)
        val byteArrays = serverCertBundle.keySet().mapNotNull { serverCertBundle[it] as? ByteArray }
        return when (byteArrays.size) {
            0 -> false // since no encoded certificate in ssl certificate
            1 -> rootCertEncoded.contentEquals(byteArrays.first())
            else -> if (BuildConfig.DEBUG) throw IllegalStateException(
                "SslCertificate is expected to contain only one byte array " +
                        "with encoded certificate in it"
            ) else {
                false
            }
        }
    }
}
