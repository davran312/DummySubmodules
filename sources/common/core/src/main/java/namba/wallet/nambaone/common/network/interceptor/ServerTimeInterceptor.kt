package namba.wallet.nambaone.common.network.interceptor

import namba.wallet.nambaone.common.date.CorrectedTimeProvider
import okhttp3.Interceptor
import okhttp3.Response
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

private const val DATE_HEADER = "Date"

class ServerTimeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        try {
            val dateHeader = response.header(DATE_HEADER)
            val date = dateHeader?.let { ZonedDateTime.parse(it, DateTimeFormatter.RFC_1123_DATE_TIME) }

            if (date != null) {
                val timestamp = date.withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli()
                CorrectedTimeProvider.setServerTime(timestamp)
            }
        } catch (e: Throwable) {
            Timber.tag("OkHttp").e(e, "Failed to parse date from response header")
        }

        return response
    }
}
