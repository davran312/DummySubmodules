package namba.wallet.nambaone.common.network.okhttp

import android.content.Context
import namba.wallet.nambaone.common.network.errors.ServerErrorInterceptor
import namba.wallet.nambaone.common.network.interceptor.ServerTimeInterceptor
import namba.wallet.nambaone.common.network.interceptor.TokenInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 60L
const val DEFAULT_READ_TIMEOUT_SECONDS = 60L
const val DEFAULT_WRITE_TIMEOUT_SECONDS = 60L
private const val DEFAULT_DISK_CACHE_SIZE = 256 * 1024 * 1024L

class BaseOkHttpClient(
    private val context: Context,
    private val tokenInterceptor: TokenInterceptor,
    private val serverTimeInterceptor: ServerTimeInterceptor,
    private val serverErrorInterceptor: ServerErrorInterceptor
) : OkHttpClientBuilder {
    override fun builder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .cache(Cache(context.cacheDir, DEFAULT_DISK_CACHE_SIZE))
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .addInterceptor(tokenInterceptor)
            .addInterceptor(serverErrorInterceptor)
            .addInterceptor(serverTimeInterceptor)
    }

    override fun build(): OkHttpClient = builder().build()
}