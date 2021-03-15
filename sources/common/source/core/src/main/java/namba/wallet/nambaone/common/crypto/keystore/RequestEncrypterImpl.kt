package namba.wallet.nambaone.common.crypto.keystore

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import namba.wallet.nambaone.common.AppCoroutineScope
import namba.wallet.nambaone.common.crypto.Base64Utils
import namba.wallet.nambaone.common.invokeOnCancelled
import namba.wallet.nambaone.common.network.interceptor.EncryptModel
import java.security.SecureRandom
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec

private const val NONCE_SIZE = 12
private const val TAG_LENGTH = 16

class RequestEncrypterImpl(
    private val appCoroutineScope: AppCoroutineScope,
    private val encryptKeystore: EncryptKeystore
) : RequestEncrypter {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val randomSecureRandom = SecureRandom()

    init {
        appCoroutineScope.invokeOnCancelled {
            dispatcher.close()
        }
    }

    override suspend fun encrypt(content: String): EncryptModel= withContext(dispatcher) {
        val ivBytes = ByteArray(NONCE_SIZE).apply {
            randomSecureRandom.nextBytes(this)
        }
        val params = GCMParameterSpec(TAG_LENGTH * Byte.SIZE_BITS, ivBytes)
        val cipher = encryptKeystore.requestCipher()
        with(encryptKeystore) {
            cipher.init(Cipher.ENCRYPT_MODE, requestSecretKeySpec(), params)
            cipher.updateAAD(requestAAD())
            val encryptedBody = cipher.doFinal(content.toByteArray())
            val tagArray = encryptedBody.copyOfRange(encryptedBody.size - TAG_LENGTH, encryptedBody.size)
            val contentArray = encryptedBody.copyOfRange(0, encryptedBody.size - TAG_LENGTH)
            val (iv,tag,bodyArray) = EncryptedContent(iv = ivBytes, tag = tagArray, content = contentArray)

            return@withContext EncryptModel(
                nonce = Base64Utils.encode(iv),
                encrypted = Base64Utils.encode(bodyArray),
                tag = Base64Utils.encode(tag),
                publicKey = Base64Utils.encode(getPublicKey())
            )
        }
    }
}