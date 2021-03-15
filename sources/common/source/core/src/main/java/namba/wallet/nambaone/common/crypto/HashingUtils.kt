package namba.wallet.nambaone.common.crypto

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import namba.wallet.nambaone.common.date.CorrectedTimeProvider

private const val SALT_GENERATE_ALGORITHM = "SHA1PRNG"
private const val SALT_SIZE = 16

private const val PIN_HASH_ALGORITHM = "PBKDF2WithHmacSHA1"
private const val MAC_ALGORITHM = "HmacSHA256"

private const val PBKDF2_ITERATIONS = 1000
private const val PBKDF2_KEY_LENGTH = 64 * 8
private const val PBKDF2_DEFAULT_LENGTH_BYTES = 64

private const val MILLS_IN_SECOND = 1000
private const val TIME_STEP_SECONDS = 10

object HashingUtils {

    fun generatePbkdf2(data: String, salt: ByteArray): String {
        val chars = data.toCharArray()

        val spec = PBEKeySpec(chars, byteArrayOf(), PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH)
        val keyFactory = SecretKeyFactory.getInstance(PIN_HASH_ALGORITHM)

        return Hex.encode(keyFactory.generateSecret(spec).encoded)
    }

    fun generatePbkdf2(data: String, salt: ByteArray, keyLengthBytes: Int = PBKDF2_DEFAULT_LENGTH_BYTES): ByteArray {
        val chars = data.toCharArray()

        val spec = PBEKeySpec(chars, byteArrayOf(), PBKDF2_ITERATIONS, keyLengthBytes * Byte.SIZE_BITS)
        val keyFactory = SecretKeyFactory.getInstance(PIN_HASH_ALGORITHM)

        return keyFactory.generateSecret(spec).encoded
    }

    fun generateShrinkedTotp(key: String) = generateTotp(key).filterIndexed { i, _ -> i.rem(2) == 0 }

    private fun generateTotp(key: String): String {
        val time = CorrectedTimeProvider.currentTimeMillis / MILLS_IN_SECOND / TIME_STEP_SECONDS
        val mac = Mac.getInstance(MAC_ALGORITHM)
        val spec = SecretKeySpec(key.toByteArray(), MAC_ALGORITHM)
        mac.init(spec)

        return Hex.encode(mac.doFinal(time.toString().toByteArray()))
    }

    fun generateSalt(): ByteArray {
        val random = SecureRandom.getInstance(SALT_GENERATE_ALGORITHM)
        val bytes = ByteArray(SALT_SIZE)
        random.nextBytes(bytes)
        return bytes
    }
}
