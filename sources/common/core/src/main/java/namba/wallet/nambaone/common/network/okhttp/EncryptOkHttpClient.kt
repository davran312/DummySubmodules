package namba.wallet.nambaone.common.network.okhttp

import com.google.gson.Gson
import namba.wallet.nambaone.common.network.interceptor.EncryptDecryptInterceptor
import namba.wallet.nambaone.common.network.interceptor.LogInterceptor
import namba.wallet.nambaone.core.BuildConfig
import okhttp3.OkHttpClient

class EncryptOkHttpClient(
    private val okHttpClient: OkHttpClientBuilder,
    private val encryptInterceptor: EncryptDecryptInterceptor,
    private val gson: Gson
) : OkHttpClientBuilder {

    override fun builder(): OkHttpClient.Builder {
        val builder = okHttpClient.builder()
        builder.addInterceptor(encryptInterceptor)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(LogInterceptor(gson).create())
        }
        return builder
    }

    override fun build(): OkHttpClient = builder().build()
}