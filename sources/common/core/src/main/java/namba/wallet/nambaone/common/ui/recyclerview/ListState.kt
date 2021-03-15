package namba.wallet.nambaone.common.ui.recyclerview

sealed class ListState {
    object Initial : ListState()
    object Idle : ListState()
    object Loading : ListState()
    object Refreshing : ListState()
    class Error(val e: Throwable) : ListState()
}
