package namba.wallet.nambaone.common.network.okhttp

import com.google.gson.Gson
import namba.wallet.nambaone.common.network.interceptor.LogInterceptor
import namba.wallet.nambaone.core.BuildConfig
import okhttp3.OkHttpClient

class ImageOkHttpClient(
    private val okHttpClient: OkHttpClientBuilder,
    private val gson: Gson
) : OkHttpClientBuilder {

    override fun builder(): OkHttpClient.Builder {
        val builder = okHttpClient.builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(LogInterceptor(gson).create())
        }
        return builder
    }

    override fun build(): OkHttpClient = builder().build()
}