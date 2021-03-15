package namba.wallet.nambaone.common.network.okhttp
import namba.wallet.nambaone.common.network.interceptor.*
import okhttp3.OkHttpClient

class SseOkHttpClient(
    private val okHttpClient: OkHttpClientBuilder,
    private val encryptInterceptor: SSeInterceptor
) : OkHttpClientBuilder {

    override fun builder(): OkHttpClient.Builder {
        val builder = okHttpClient.builder()
        builder.addInterceptor(encryptInterceptor)

        okHttpClient.builder().addInterceptor(
            encryptInterceptor
        )
        return builder
    }

    override fun build(): OkHttpClient = builder().build()
}