package namba.wallet.nambaone.uikit.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_webview.*
import namba.wallet.nambaone.common.utils.args
import namba.wallet.nambaone.common.utils.navigation.popScreen
import namba.wallet.nambaone.common.utils.withArgs
import namba.wallet.nambaone.uikit.R
import namba.wallet.nambaone.uikit.extensions.redispatchWindowInsetsToAllChildren

const val EXTRA_HTML_PAGE = "EXTRA_HTML_PAGE"
const val EXTRA_SUCCESS_URL = "EXTRA_REDIRECT_URL"

open class HtmlViewFragment : Fragment(R.layout.fragment_webview) {

    companion object {
        fun create(
            htmlPage: String,
            redirectUrl: String
        ) = HtmlViewFragment().withArgs(
            EXTRA_HTML_PAGE to htmlPage,
            EXTRA_SUCCESS_URL to redirectUrl
        )
    }

    private val htmlPage: String by args(EXTRA_HTML_PAGE)
    private val successUrl: String by args(EXTRA_SUCCESS_URL)

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
                if (url == successUrl) {
                    onRedirectUrlSucceed()
                    return true
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
        }
        errorView.onRefreshClickListener = {
            webView.loadDataWithBaseURL(null, htmlPage, "text/html", "utf-8", null)
        }
        webView.loadDataWithBaseURL(null, htmlPage, "text/html", "utf-8", null)
        applyInsets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.webViewClient = null
        webView.webChromeClient = null
    }
    open fun onRedirectUrlSucceed() {}
    private fun applyInsets() {
        webViewContainer.redispatchWindowInsetsToAllChildren()
        nambaToolBar.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
    }
}
