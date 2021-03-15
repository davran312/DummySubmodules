package namba.wallet.nambaone.common.crypto.keystore

import namba.wallet.nambaone.common.network.interceptor.EncryptModel

interface RequestEncrypter {
    suspend fun encrypt(content: String): EncryptModel
}