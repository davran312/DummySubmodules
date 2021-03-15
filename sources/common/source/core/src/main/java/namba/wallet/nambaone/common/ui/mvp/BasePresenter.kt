package namba.wallet.nambaone.common.ui.mvp

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.coroutines.CoroutineContext

open class BasePresenter<V : MvpView> : ViewModel(), MvpPresenter<V>, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    var view: V? = null
        private set

    @CallSuper
    override fun attach(view: V) {
        this.view = view
    }

    @CallSuper
    override fun detach() {
        view = null
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}

@MainThread
inline fun <TView, reified TPresenter> TView.presenter(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
)
    where TView : Fragment, TView : MvpView,
          TPresenter : ViewModel, TPresenter : MvpPresenter<in TView> =
    viewModel<TPresenter>(qualifier,parameters =  parameters).also {
        lifecycle.addObserver(
            FragmentLifecycleObserver(it, this)
        )
    }


@MainThread
inline fun <TView, reified TPresenter> TView.sharedPresenter(
    qualifier: Qualifier? = null,
    noinline parameters: BundleDefinition? = null
)
    where TView : Fragment, TView : MvpView,
          TPresenter : ViewModel, TPresenter : MvpPresenter<in TView> =
    sharedViewModel<TPresenter>(qualifier, parameters).also {
        lifecycle.addObserver(
            FragmentLifecycleObserver(it, this)
        )
    }

@PublishedApi
internal class FragmentLifecycleObserver<TView, TPresenter>(
    private val lazyPresenter: Lazy<TPresenter>,
    private val fragment: TView
) : LifecycleObserver
    where TView : Fragment, TView : MvpView,
          TPresenter : ViewModel, TPresenter : MvpPresenter<in TView> {

    private val observer = Observer<LifecycleOwner> { lifecycleOwner ->
        lifecycleOwner?.run {
            // viewLifecycleOwner диспатчится в liveData только после создания view,
            // следовательно можем сразу позвать attach.
            // В случае, если мы попробуем сделать attach в ViewLifecycleObserver на Lifecycle.Event.ON_CREATE,
            // то attach отработает только после Fragment#onViewCreated, т.к. во viewLifecycleOwner состояния начинают
            // диспатчится только после Fragment#onViewCreated
            lifecycle.addObserver(ViewLifecycleObserver())
            lazyPresenter.value.attach(fragment)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        fragment.viewLifecycleOwnerLiveData.observeForever(observer)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.removeObserver(observer)
        owner.lifecycle.removeObserver(this)
    }

    private inner class ViewLifecycleObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            lazyPresenter.value.detach()
        }
    }
}

