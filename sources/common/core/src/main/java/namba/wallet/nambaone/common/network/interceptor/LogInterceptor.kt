package namba.wallet.nambaone.common.network.interceptor

import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class LogInterceptor(private val gson: Gson) {

    fun create(): HttpLoggingInterceptor {
        val tag = "OkHttp"
        val okHttpLogger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!message.startsWith('{') && !message.startsWith('[')) {
                    Timber.tag(tag).d(message)
                    return
                }

                try {
                    val json = JsonParser.parseString(message)
                    Timber.tag(tag).d(gson.toJson(json))
                } catch (e: Throwable) {
                    Timber.tag(tag).d(message)
                }
            }
        }
        return HttpLoggingInterceptor(okHttpLogger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}