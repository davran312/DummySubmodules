package namba.wallet.nambaone.common.ui.mvp

import android.app.Activity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.whenCreated
import kotlinx.android.synthetic.main.fragment_container.*
import kotlinx.android.synthetic.main.widget_progress_view.view.*
import namba.wallet.nambaone.common.utils.extensions.showInfoDialog
import namba.wallet.nambaone.common.utils.navigation.getCurrentScreen
import namba.wallet.nambaone.common.utils.navigation.popScreen
import namba.wallet.nambaone.core.R
import timber.log.Timber

private const val EXTRA_OVERRIDDEN_ENTER_ANIMATION = "EXTRA_OVERRIDDEN_ENTER_ANIMATION"
private const val EXTRA_OVERRIDDEN_EXIT_ANIMATION = "EXTRA_OVERRIDDEN_EXIT_ANIMATION"

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes), MvpView {
    protected var backPressedCallback: OnBackPressedCallback? = null
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val extra = if (enter) EXTRA_OVERRIDDEN_ENTER_ANIMATION else EXTRA_OVERRIDDEN_EXIT_ANIMATION
        val animRes = arguments?.getInt(extra)?.takeIf { it != 0 }
            ?: return super.onCreateAnimation(transit, enter, nextAnim)

        requireArguments().remove(extra)

        return AnimationUtils.loadAnimation(requireContext(), animRes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setDarkStatusBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            popScreen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.isClickable = true
        view?.isFocusableInTouchMode = true
        return view
    }

    fun overrideEnterAnimation(@AnimRes animation: Int) {
        overrideAnimation(animation, EXTRA_OVERRIDDEN_ENTER_ANIMATION)
    }

    fun overrideExitAnimation(@AnimRes animation: Int) {
        overrideAnimation(animation, EXTRA_OVERRIDDEN_EXIT_ANIMATION)
    }

    private fun overrideAnimation(@AnimRes animation: Int, extraKey: String) {
        arguments = (arguments ?: Bundle()).apply { putInt(extraKey, animation) }
    }

    override fun showErrorMessage(e: Throwable?) {
        Timber.e(e)
        showInfoDialog(e)
    }

    override fun showErrorMessage(messageRes: Int) {
        showInfoDialog(messageRes)
    }

    fun showPageLoading(isLoading: Boolean) {
        val fragment = parentFragmentManager.fragments.filterNotNull().first()
        val group = fragment.view?.parent as ViewGroup
        val tagView = group.getTag(R.id.progress_view_id) as? ConstraintLayout
        if (tagView != null) {
            group.progressView.isVisible = isLoading
        } else {
            val view = layoutInflater.inflate(R.layout.widget_progress_view, group, false)
            group.addView(view)
            group.setTag(R.id.progress_view_id, view)
        }
    }
}

fun Activity.setDarkStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    // window.statusBarColor = ContextCompat.getColor(BookReaderActivity.this,R.color.white);// set status background white
}

fun Activity.setLightStatusBar() {
//        window.statusBarColor = ContextCompat.getColor(,R.color.black)
    val decorView = window.decorView //set status background black
    decorView.systemUiVisibility =
        decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
}

