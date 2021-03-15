package namba.wallet.nambaone.common.update

interface UpdatesStateObserver {

    companion object {
        operator fun invoke(updateHandler: (UpdatesState) -> Unit) = object : UpdatesStateObserver {
            override fun onUpdatesStateChanged(state: UpdatesState) {
                updateHandler(state)
            }
        }
    }

    fun onUpdatesStateChanged(state: UpdatesState)
}
