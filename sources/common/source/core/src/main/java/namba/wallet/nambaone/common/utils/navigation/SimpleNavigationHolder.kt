package namba.wallet.nambaone.common.utils.navigation

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import namba.wallet.nambaone.common.utils.AndroidUtils

class SimpleNavigationHolder : NavigatorsHolder {
    private val navigatorDefinitions = mutableMapOf<String, Lazy<BaseNavigator>>()

    override fun registerNavigator(
        key: String,
        definition: Lazy<BaseNavigator>
    ) {
        navigatorDefinitions[key] = definition
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BaseNavigator> getNavigator(key: String): T =
        navigatorDefinitions[key]?.value as? T
            ?: throw IllegalArgumentException("No registered navigator for $key")
}

@MainThread
inline fun <reified T : BaseNavigator> Fragment.navigator(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        check(AndroidUtils.isMainThread()) { "Navigator has to be acquired from main thread" }

        val holder = parentFragment as? NavigatorsHolder
            ?: parentFragment?.parentFragment?.let { it as? NavigatorsHolder }
            ?: requireActivity() as? NavigatorsHolder
            ?: throw IllegalStateException("No navigation holder parent")

        holder.getNavigator<T>(T::class.java.name)
    }
}

@MainThread
inline fun <reified T : BaseNavigator> NavigatorsHolder.registerNavigator(noinline definition: () -> T) {
    check(AndroidUtils.isMainThread()) { "Navigator has to be registered from main thread" }

    registerNavigator(
        key = findBaseNavigatorDescendantInterface(T::class.java).name,
        definition = lazy(LazyThreadSafetyMode.NONE, definition)
    )
}

@PublishedApi
internal tailrec fun findBaseNavigatorDescendantInterface(type: Class<*>): Class<*> =
    type.interfaces.firstOrNull { BaseNavigator::class.java.isAssignableFrom(it) }
        ?: findBaseNavigatorDescendantInterface(type.superclass as Class<*>)
