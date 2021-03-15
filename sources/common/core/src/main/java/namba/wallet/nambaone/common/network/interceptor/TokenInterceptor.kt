package namba.wallet.nambaone.common.network.interceptor

import namba.wallet.nambaone.common.network.provider.TokenProvider
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (tokenProvider.token.isEmpty()) {
            return chain.proceed(chain.request())
        }
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type","application/json")
            .addHeader("Accept","application/json")
            .addHeader("x-auth-session-id", tokenProvider.token)
            .build()
        return chain.proceed(request)
    }
}
