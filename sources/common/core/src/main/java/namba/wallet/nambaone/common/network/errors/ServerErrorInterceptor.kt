package namba.wallet.nambaone.common.network.errors

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import namba.wallet.nambaone.common.applock.AppLockInteractor
import okhttp3.Interceptor
import okhttp3.Response
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.io.IOException

class ServerErrorInterceptor(
        private val gson: Gson,
        private val appLockInteractor: AppLockInteractor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isSuccessful) {
            return response
        } else {
            throw extractException(response)
        }
    }

    private fun extractException(response: Response): Throwable {
        val statusCode = response.code
        return try {
            val serverError = response.body!!.use {
                gson.fromJson<ServerError>(JsonReader(it.charStream()), ServerError::class.java)
            }

            ServerException(
                    statusCode = statusCode,
                    serverError = serverError
            )
        } catch (e: Throwable) {
            ServerException(
                    statusCode = statusCode,
                    serverError = ServerError(
                            status = "$statusCode",
                            errorDetail = ErrorDetail(
                                    errorCode = ErrorCode.Error,
                                    description = "",
                                    message = null,
                                    data = null,
                                    timestamp = ZonedDateTime.now()
                            )
                    )
            )
        }
    }
}
