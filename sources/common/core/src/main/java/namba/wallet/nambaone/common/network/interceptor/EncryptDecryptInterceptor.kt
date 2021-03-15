package namba.wallet.nambaone.common.network.interceptor

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.runBlocking
import namba.wallet.nambaone.common.crypto.keystore.RequestEncrypter
import namba.wallet.nambaone.common.crypto.keystore.ResponseDecrypter
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import timber.log.Timber

class EncryptDecryptInterceptor(
    private val gson: Gson,
    private val encrypter: RequestEncrypter,
    private val decrypter: ResponseDecrypter,
    private val publicKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        try {
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("publicKey", publicKey)
                .addHeader("Content-Type", "application/json")
            val body = request.body
            if (body != null) {
                val bodyString = Buffer().apply { body.writeTo(this) }.readUtf8()
                Timber.tag("OkHttp").d("Decrypted request body: $bodyString")
                val encryptBody = runBlocking { encrypter.encrypt(bodyString) }
                Timber.tag("OkHttp").d("Encrypted request body: $encryptBody")
                newRequest.method(request.method, gson.toJson(encryptBody).toRequestBody())
            }
            response = chain.proceed(newRequest.build())
            val responseBody = response.body ?: return response
            val responseBodyString = responseBody.string()
            Timber.tag("OkHttp").d("Encrypted response body: $responseBodyString")
            //Try to  parse response as encrypted model otherwise return normal response
            return try {
                val model = gson.fromJson(responseBodyString, EncryptModel::class.java)
                Timber.tag("OkHttp").d("Parsed json model $model")
                val decryptedResponseBody = runBlocking {
                    decrypter.decrypt(
                        decrypter.getIV(model.nonce),
                        model.cipherText()
                    )
                }
                Timber.tag("OkHttp").d("Decrypted response body: $decryptedResponseBody")
                response.newBuilder().body(decryptedResponseBody.toResponseBody()).build()
            } catch (e: JsonSyntaxException) {
                chain.proceed(chain.request())
            }
        } catch (e: Throwable) {
            return chain.proceed(chain.request())
        }
    }
}