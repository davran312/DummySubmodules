package namba.wallet.nambaone.common.applock

import namba.wallet.nambaone.common.crypto.SecureStorage

sealed class LockState {
    companion object {
        private const val STATE_ID_KEY = "STATE_ID"

        internal fun restore(secureStorage: SecureStorage): LockState = when (secureStorage.getString(STATE_ID_KEY)) {
            "1" -> Idle
            "2" -> {
                LockScheduled(secureStorage.getString(LockScheduled.LOCK_TIME_KEY)?.toLongOrNull())
                    .takeIf { !it.isTimedOut() }
                    ?: Locked
            }
            "4" -> KfmBlocked
            else -> Locked
        }

        internal fun clearPersisted(secureStorage: SecureStorage) {
            secureStorage.remove(STATE_ID_KEY)
            secureStorage.remove(LockScheduled.LOCK_TIME_KEY)
        }
    }

    object Idle : LockState()
    object Locked : LockState()
    object KfmBlocked : LockState()
    data class LockScheduled
    internal constructor(val lockTime: Long? = System.currentTimeMillis() + LOCK_TIMEOUT) : LockState() {
        companion object {
            const val LOCK_TIME_KEY = "LOCK_TIME"
            private const val LOCK_TIMEOUT = 60_000L
        }

        internal fun isTimedOut(): Boolean {
            return lockTime == null || lockTime < System.currentTimeMillis()
        }
    }

    internal fun persist(secureStorage: SecureStorage) {
        when (this) {
            Idle -> secureStorage.saveString(STATE_ID_KEY, "1")
            is LockScheduled -> {
                secureStorage.saveString(STATE_ID_KEY, "2")
                secureStorage.saveString(LockScheduled.LOCK_TIME_KEY, lockTime.toString())
            }
            Locked -> secureStorage.saveString(STATE_ID_KEY, "3")
            KfmBlocked -> secureStorage.saveString(STATE_ID_KEY, "4")
        }
    }
}
