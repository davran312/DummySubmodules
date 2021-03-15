package namba.wallet.nambaone.common.crypto.keystore

data class EncryptedContent(
    val iv: ByteArray,
    val tag: ByteArray,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedContent

        if (!iv.contentEquals(other.iv)) return false
        if (!tag.contentEquals(other.tag)) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + tag.contentHashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}