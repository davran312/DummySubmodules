package namba.wallet.nambaone.common.ui.mvp

interface MvpPresenter<V : MvpView> {

    fun attach(view: V)

    fun detach()
}
