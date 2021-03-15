package namba.wallet.nambaone.uikit.widget

interface ScrollConsumingView {
    fun consumeVerticalScroll(dy: Int): Int
    fun getOffset(): Int
    fun onScrollStart()
    fun onScrollEnd()
}
