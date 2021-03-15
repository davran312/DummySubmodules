
package namba.wallet.nambaone.common.utils.navigation

import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.whenStateAtLeast
import kotlin.reflect.KClass
import kotlinx.coroutines.launch
import namba.wallet.nambaone.common.ui.mvp.BaseFragment
import namba.wallet.nambaone.common.utils.appendArgs
import namba.wallet.nambaone.common.utils.extensions.hideKeyboard
import namba.wallet.nambaone.core.R

private const val PREVIOUS_FRAGMENT_TAG_ARG = "PREVIOUS_FRAGMENT_TAG_ARG"

private inline fun LifecycleOwner.whenStateAtLeast(
    state: Lifecycle.State,
    crossinline block: () -> Unit
) {
    if (lifecycle.currentState.isAtLeast(state)) {
        block()
    } else {
        lifecycle.coroutineScope.launch {
            lifecycle.whenStateAtLeast(state) { block() }
        }
    }
}

private fun Fragment.getPreviousTag(): String? = arguments?.getString(PREVIOUS_FRAGMENT_TAG_ARG)
fun Fragment.getCurrentScreen(): Fragment? =
    childFragmentManager.findFragmentById(R.id.featureContent)

fun FragmentActivity.getCurrentFeature() =
    supportFragmentManager.findFragmentById(R.id.activityContent)

fun <T : Fragment> FragmentActivity.findFeature(target: KClass<T>) =
    supportFragmentManager.findFragmentByTag(target.java.name)

fun Fragment.popScreen() {
    requireActivity().hideKeyboard()

    val fragmentManager = parentFragment?.childFragmentManager ?: childFragmentManager
    if (fragmentManager.backStackEntryCount < 2) {
        requireActivity().popFeature()
    } else {
        whenStateAtLeast(Lifecycle.State.STARTED) { fragmentManager.popBackStack() }
    }
}

fun FragmentActivity.popFeature() {
    if (supportFragmentManager.backStackEntryCount < 2) {
        finish()
    } else {
        whenStateAtLeast(Lifecycle.State.STARTED) { supportFragmentManager.popBackStack() }
    }
}

fun <T : Fragment> Fragment.popScreenTo(
    target: KClass<T>,
    inclusive: Boolean = false
): Boolean {
    requireActivity().hideKeyboard()

    val flag = if (inclusive) POP_BACK_STACK_INCLUSIVE else 0
    val tag = target.java.name

    return with(parentFragment?.childFragmentManager ?: childFragmentManager) {
        if (
            backStackEntryCount < 2 ||
            (getBackStackEntryAt(0).name == tag && inclusive) ||
            findFragmentByTag(tag) == null
        ) {
            requireActivity().popFeature()
            false
        } else {
            whenStateAtLeast(Lifecycle.State.STARTED) { popBackStack(tag, flag) }
            true
        }
    }
}

fun <T : Fragment> Fragment.popScreenToImmediately(
    target: KClass<T>,
    inclusive: Boolean = false
) {
    requireActivity().hideKeyboard()

    val flag = if (inclusive) POP_BACK_STACK_INCLUSIVE else 0
    val tag = target.java.name

    return with(parentFragment?.childFragmentManager ?: childFragmentManager) {
        whenStateAtLeast(Lifecycle.State.STARTED) { popBackStack(tag, flag) }
    }
}

fun <T : Fragment> Fragment.popScreenToIfPossible(
    target: KClass<T>,
    inclusive: Boolean = false
): Boolean {
    requireActivity().hideKeyboard()

    val flag = if (inclusive) POP_BACK_STACK_INCLUSIVE else 0
    val tag = target.java.name

    return with(parentFragment?.childFragmentManager ?: childFragmentManager) {
        if (
            backStackEntryCount < 2 ||
            getBackStackEntryAt(0).name == tag && inclusive ||
            findFragmentByTag(tag) == null
        ) {
            false
        } else {
            whenStateAtLeast(Lifecycle.State.STARTED) { popBackStack(tag, flag) }
            true
        }
    }
}

fun <T : Fragment> FragmentActivity.popFeatureTo(
    target: KClass<T>,
    inclusive: Boolean = false
) {
    hideKeyboard()

    val flag = if (inclusive) POP_BACK_STACK_INCLUSIVE else 0
    val tag = target.java.name

    with(supportFragmentManager) {
        if (
            backStackEntryCount < 2 ||
            getBackStackEntryAt(0).name == tag && inclusive ||
            findFragmentByTag(tag) == null
        ) {
            finish()
        } else {
            whenStateAtLeast(Lifecycle.State.STARTED) { popBackStack(tag, flag) }
        }
    }
}

fun Fragment.popAndReplaceScreen(
    replaceTarget: Fragment,
    popTo: KClass<out Fragment>? = null,
    inclusive: Boolean = false,
    addToBackStack: Boolean = true,
    @IdRes layoutId: Int = R.id.featureContent,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit,
    fragmentManager: FragmentManager = parentFragment?.childFragmentManager ?: childFragmentManager
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    requireActivity().hideKeyboard()

    with(fragmentManager) {
        // Override exit animation for popping fragment
        val poppingFragment =
            if (parentFragment == null) getCurrentScreen() else this@popAndReplaceScreen
        if (poppingFragment is BaseFragment) {
            poppingFragment.overrideExitAnimation(exit)
        }

        // Make pop entering fragment invisible during transition
        popTo?.java?.name
            ?.let { findFragmentByTag(it) as? BaseFragment }
            ?.apply { overrideEnterAnimation(R.anim.nav_stay_transparent) }

        // Perform pop back stack
        popBackStack(
            popTo?.java?.name,
            if (inclusive && popTo != null) POP_BACK_STACK_INCLUSIVE else 0
        )

        commit {
            // Preform immediate replace
            setCustomAnimations(enter, 0, popEnter, popExit)
            replace(layoutId, replaceTarget, replaceTarget.javaClass.name)
            if (addToBackStack) addToBackStack(replaceTarget.javaClass.name)
        }
    }
}

fun FragmentActivity.popAndReplaceFeature(
    replaceTarget: Fragment,
    popTo: KClass<out Fragment>? = null,
    inclusive: Boolean = false,
    @IdRes layoutId: Int = R.id.activityContent,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    hideKeyboard()

    with(supportFragmentManager) {
        // Override exit animation for popping fragment
        val poppingFragment = getCurrentFeature()
        if (poppingFragment is BaseFragment) {
            poppingFragment.overrideExitAnimation(exit)
        }

        // Make pop entering fragment invisible during transition
        popTo?.java?.name
            ?.let { findFragmentByTag(it) as? BaseFragment }
            ?.apply { overrideEnterAnimation(R.anim.nav_stay_transparent) }

        // Perform pop back stack
        popBackStack(
            popTo?.java?.name,
            if (inclusive && popTo != null) POP_BACK_STACK_INCLUSIVE else 0
        )

        commit {
            setCustomAnimations(enter, 0, popEnter, popExit)
            replace(layoutId, replaceTarget, replaceTarget.javaClass.name)
            addToBackStack(replaceTarget.javaClass.name)
        }
    }
}

fun Fragment.addScreen(
    fragment: Fragment,
    addToBackStack: Boolean = true,
    requestCode: Int? = null,
    tag: String = fragment::class.java.name,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit,
    fragmentManager: FragmentManager = parentFragment?.childFragmentManager ?: childFragmentManager
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    requireActivity().hideKeyboard()
    fragmentManager.commit {
        setCustomAnimations(enter, exit, popEnter, popExit)
        add(R.id.featureContent, fragment)
        if (addToBackStack) addToBackStack(tag)
        if (requestCode != null) fragment.setTargetFragment(this@addScreen, requestCode)
    }
}

fun Fragment.replaceScreen(
    fragment: Fragment,
    popCurrent: Boolean = false,
    addToBackStack: Boolean = true,
    requestCode: Int? = null,
    tag: String = fragment::class.java.name,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit,
    fragmentManager: FragmentManager = parentFragment?.childFragmentManager ?: childFragmentManager
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    requireActivity().hideKeyboard()
    fragmentManager.commit {
        setCustomAnimations(enter, exit, popEnter, popExit)
        if (popCurrent) {
            getCurrentScreen()
                ?.let { it.getPreviousTag() ?: it::class.java.name }
                ?.let { fragment.appendArgs(PREVIOUS_FRAGMENT_TAG_ARG to it) }
        }
        replace(R.id.featureContent, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
        if (requestCode != null) fragment.setTargetFragment(this@replaceScreen, requestCode)
    }
}

fun FragmentActivity.replaceFeature(
    fragment: Fragment,
    @IdRes layoutId: Int = R.id.activityContent,
    tag: String = fragment::class.java.name,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    hideKeyboard()
    supportFragmentManager.commit {
        setCustomAnimations(enter, exit, popEnter, popExit)
        replace(layoutId, fragment)
        addToBackStack(tag)
    }
}

fun FragmentActivity.addFeature(
    fragment: Fragment,
    @IdRes layoutId: Int = R.id.activityContent,
    tag: String = fragment::class.java.name,
    @AnimRes enter: Int = R.anim.nav_enter,
    @AnimRes exit: Int = R.anim.nav_exit,
    @AnimRes popEnter: Int = R.anim.nav_pop_enter,
    @AnimRes popExit: Int = R.anim.nav_pop_exit
) = whenStateAtLeast(Lifecycle.State.STARTED) {
    hideKeyboard()
    supportFragmentManager.commit {
        setCustomAnimations(enter, exit, popEnter, popExit)
        add(layoutId, fragment)
        addToBackStack(tag)
    }
}
