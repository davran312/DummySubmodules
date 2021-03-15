package namba.wallet.nambaone.common.date

object CorrectedTimeProvider {
    @Volatile
    private var diff = 0L

    val currentTimeMillis: Long
        get() = System.currentTimeMillis() + diff

    fun setServerTime(serverTimeMillis: Long) {
        diff = serverTimeMillis - System.currentTimeMillis()
    }
}
