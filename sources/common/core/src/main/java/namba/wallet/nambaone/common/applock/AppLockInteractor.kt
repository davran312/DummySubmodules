package namba.wallet.nambaone.common.applock

import java.util.concurrent.Executors
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import namba.wallet.nambaone.common.AppCoroutineScope
import namba.wallet.nambaone.common.crypto.SecureStorage

class AppLockInteractor(
    private val secureStorage: SecureStorage,
    private val appCoroutineScope: AppCoroutineScope
) {

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val state: MutableStateFlow<LockState> =
        MutableStateFlow(LockState.restore(secureStorage))

    init {
        LockState.clearPersisted(secureStorage)
    }

    fun onForeground() = updateState {
        LockState.clearPersisted(secureStorage)
        if (it !is LockState.LockScheduled) {
            return@updateState it
        }

        if (it.isTimedOut()) LockState.Locked
        else LockState.Idle
    }

    fun onBackground() = updateState {
        val newState = if (it is LockState.Idle) LockState.LockScheduled() else it
        newState.persist(secureStorage)
        return@updateState newState
    }

    fun unlock() = updateState {
        if (it is LockState.Locked || it is LockState.LockScheduled) LockState.Idle
        else it
    }

    fun lock() = updateState {
        if (it is LockState.Idle || it is LockState.LockScheduled) LockState.Locked
        else it
    }

    fun block() = updateState { LockState.KfmBlocked }

    fun getIsLockedFlow(): Flow<LockState> = state

    private fun updateState(updateFunc: (LockState) -> LockState) {
        appCoroutineScope.launch(dispatcher) {
            state.value = updateFunc(state.value)
        }
    }
}
