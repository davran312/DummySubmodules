package namba.wallet.nambaone.common.crypto.keystore

import kotlinx.coroutines.asCoroutineDispatcher
import namba.wallet.nambaone.common.AppCoroutineScope
import namba.wallet.nambaone.common.crypto.Base64Utils
import namba.wallet.nambaone.common.invokeOnCancelled
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.SecretKeySpec

class EncryptKeystore(
    private val serverKey: String,
    appCoroutineScope: AppCoroutineScope
) {
    private val serverX509ks by lazy { X509EncodedKeySpec(Base64Utils.decode(serverKey)) }
    private val serverPubKey: PublicKey by lazy { keyFactory.generatePublic(serverX509ks) }
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        appCoroutineScope.invokeOnCancelled {
            dispatcher.close()
        }
    }

    private val keyFactory by lazy { KeyFactory.getInstance("ECDH", "SC") }
    private val kpg = KeyPairGenerator.getInstance("ECDH", "SC")
        .apply { initialize(256) }
    private val kpA: KeyPair by lazy { kpg.generateKeyPair() }

    private val publString: String = Base64Utils.encode(kpA.public.encoded)
    private val privString: String = Base64Utils.encode(kpA.private.encoded)

    private val x509ks by lazy { X509EncodedKeySpec(Base64Utils.decode(publString)) }
    private val pubKeyA: PublicKey by lazy { keyFactory.generatePublic(x509ks) }

    private val p8ks by lazy { PKCS8EncodedKeySpec(Base64Utils.decode(privString)) }
    private val privKeyA: PrivateKey by lazy { keyFactory.generatePrivate(p8ks) }

    private val aKA: KeyAgreement by lazy {
        KeyAgreement.getInstance("ECDH", "SC").apply {
            init(privKeyA)
            doPhase(serverPubKey, true)
        }
    }
    private val sharedKeyA: ByteArray by lazy { aKA.generateSecret() }
    private val cipher by lazy { Cipher.getInstance("AES/GCM/NoPadding") }
    private val aad = ByteArray(0)
    private val spec = SecretKeySpec(sharedKeyA, "AES")

    fun getPublicKey(): ByteArray = pubKeyA.encoded

    fun getPublicKeyString() = Base64Utils.encode(getPublicKey())

    fun requestCipher(): Cipher = cipher

    fun requestSecretKeySpec() = spec

    fun requestAAD() = aad
}

