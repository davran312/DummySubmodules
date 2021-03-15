package namba.wallet.nambaone.common.network.interceptor

import com.google.gson.annotations.SerializedName
import namba.wallet.nambaone.common.crypto.Base64Utils

data class EncryptModel(
    @SerializedName("nonce")
    val nonce: String,
    @SerializedName("encrypted")
    val encrypted: String,
    @SerializedName("tag")
    val tag: String,
    @SerializedName("publickey")
    val publicKey: String?
) {

    fun cipherText(): ByteArray {
        val textBytes = Base64Utils.decode(encrypted)
        val tagBytes = Base64Utils.decode(tag)
        val newByteArray = ByteArray(textBytes.size + tagBytes.size)
        System.arraycopy(textBytes, 0, newByteArray, 0, textBytes.size)
        System.arraycopy(tagBytes, 0, newByteArray, textBytes.size, tagBytes.size)
        return newByteArray
    }
}