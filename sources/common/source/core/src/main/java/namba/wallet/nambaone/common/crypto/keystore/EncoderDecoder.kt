package namba.wallet.nambaone.common.crypto.keystore

import java.security.KeyStore
import javax.crypto.Cipher

interface EncoderDecoder {
    fun setKeyStore(keyStore: KeyStore, providerName: String)

    fun encode(keyAlias: String, data: ByteArray): ByteArray
    fun decode(keyAlias: String, encodedData: ByteArray): ByteArray

    fun createSecuredKey(keyAlias: String)
    fun getCipher(keyAlias: String, cipherMode: Int): Cipher

    fun onKeyDeleted(keyAlias: String)
}
