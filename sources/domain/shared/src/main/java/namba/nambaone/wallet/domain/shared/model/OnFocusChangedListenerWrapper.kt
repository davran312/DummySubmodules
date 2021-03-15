package namba.nambaone.wallet.domain.shared.model

import android.view.View

class OnFocusChangedListenerWrapper(
    private val wrapped: View.OnFocusChangeListener?,
    private val delegate: View.OnFocusChangeListener
) : View.OnFocusChangeListener {

    companion object {
        fun installOn(view: View, delegate: View.OnFocusChangeListener) {
            val wrapped = view.onFocusChangeListener
            val wrapper = OnFocusChangedListenerWrapper(wrapped, delegate)
            view.onFocusChangeListener = wrapper
        }

        fun uninstallWrappersChain(view: View) {
            var unwrapped: View.OnFocusChangeListener? = view.onFocusChangeListener
            while (unwrapped is OnFocusChangedListenerWrapper) {
                unwrapped = unwrapped.wrapped
            }
            view.onFocusChangeListener = unwrapped
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        wrapped?.onFocusChange(v, hasFocus)
        delegate.onFocusChange(v, hasFocus)
    }
}
