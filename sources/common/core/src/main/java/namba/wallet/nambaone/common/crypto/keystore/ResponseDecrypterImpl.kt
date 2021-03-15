package namba.wallet.nambaone.common.crypto.keystore

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import namba.wallet.nambaone.common.AppCoroutineScope
import namba.wallet.nambaone.common.crypto.Base64Utils
import namba.wallet.nambaone.common.invokeOnCancelled
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

private const val TAG_LENGTH = 16

class ResponseDecrypterImpl(
    appCoroutineScope: AppCoroutineScope,
    private val encryptKeystore: EncryptKeystore
) : ResponseDecrypter {

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        appCoroutineScope.invokeOnCancelled {
            dispatcher.cancel()
        }
    }

    override suspend fun decrypt(iv: ByteArray, encryptedContent: ByteArray): String = withContext(dispatcher) {
        val params = GCMParameterSpec(TAG_LENGTH * Byte.SIZE_BITS, iv)
        val cipher = encryptKeystore.requestCipher()
        with(encryptKeystore) {
            cipher.init(Cipher.DECRYPT_MODE, requestSecretKeySpec(), params)
            cipher.updateAAD(requestAAD())
            val responseBytes = cipher.doFinal(encryptedContent)
            String(responseBytes)
        }
    }

    override fun getIV(nonce: String): ByteArray = Base64Utils.decode(nonce)
}