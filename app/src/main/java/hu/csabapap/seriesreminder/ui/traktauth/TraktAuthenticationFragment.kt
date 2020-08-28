package hu.csabapap.seriesreminder.ui.traktauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.network.services.AuthService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TraktAuthenticationFragment : DaggerFragment() {

    @Inject
    lateinit var authService: AuthService

    lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trakt_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.web_view)

        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Timber.d(url)
                if (url.startsWith("http://localhost")) {
                    val code = request?.url?.getQueryParameter("code") ?: ""
                    requestToken(code)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        webView.loadUrl("https://trakt.tv/oauth/authorize?response_type=code&client_id=${BuildConfig.TRAKT_CLIENT_ID}&redirect_uri=http%3A%2F%2Flocalhost")
    }

    private fun requestToken(code: String) {
        GlobalScope.launch {
            val result = authService.requestToken(code, BuildConfig.TRAKT_CLIENT_ID, BuildConfig.TRAKT_SECRET_ID, "http://localhost")

            Timber.d("result: $result")
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                TraktAuthenticationFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}