package namba.wallet.nambaone.common.crypto.keystore

interface ResponseDecrypter {
    suspend fun decrypt(iv: ByteArray, encryptedContent: ByteArray): String
    fun getIV(nonce: String): ByteArray
}