package namba.wallet.nambaone.common.network.provider

import namba.wallet.nambaone.common.crypto.SecureStorage

private const val KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN"

@Suppress("UseDataClass")
class TokenProvider(private val secureStorage: SecureStorage) {
    var token: String = secureStorage.getString(KEY_SESSION_TOKEN) ?: ""
        set(value) {
            field = value
            secureStorage.saveString(KEY_SESSION_TOKEN, value)
        }
}
