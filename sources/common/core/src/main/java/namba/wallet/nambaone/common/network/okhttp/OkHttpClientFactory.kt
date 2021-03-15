package namba.wallet.nambaone.common.network.okhttp

import okhttp3.OkHttpClient
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object OkHttpClientFactory : KoinComponent {

    fun getInstance(type: Type): OkHttpClient {
        return when (type) {
            Type.SSE -> {
                SseOkHttpClient(get(), get())
            }
            Type.DEFAULT -> {
                EncryptOkHttpClient(get(), get(), get())
            }
            Type.MULTIPART -> {
                ImageOkHttpClient(get(), get())
            }
        }.build()
    }

    enum class Type {
        SSE,
        MULTIPART,
        DEFAULT,
    }
}