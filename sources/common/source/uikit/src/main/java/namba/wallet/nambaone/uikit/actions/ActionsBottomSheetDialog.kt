package namba.wallet.nambaone.uikit.actions

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_actions_bottom_sheet.actionsRecyclerView
import kotlinx.android.synthetic.main.dialog_actions_bottom_sheet.topTextView
import namba.wallet.nambaone.common.utils.extensions.attachAdapter
import namba.wallet.nambaone.uikit.R

class ActionsBottomSheetDialog : BottomSheetDialogFragment() {

    companion object {

        fun show(
            activity: FragmentActivity,
            title: String? = null,
            actions: List<Action>
        ) {
            show(activity.supportFragmentManager, title, actions)
        }

        fun show(
            fragment: Fragment,
            title: String? = null,
            actions: List<Action>
        ) {
            show(fragment.childFragmentManager, title, actions)
        }

        private fun show(fm: FragmentManager?, title: String?, actions: List<Action>) {
            if (fm == null) return
            ActionsBottomSheetDialog()
                .apply {
                    this.title = title
                    this.actions = actions
                    this.themeRes = R.style.BaseBottomSheetDialog
                }
                .show(fm, ActionsBottomSheetDialog::class.java.simpleName)
        }
    }

    private var title: String? = null
    private var actions: List<Action>? = null

    @StyleRes
    private var themeRes: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), themeRes)
        // dirty hack to prevent dialog restoring
        if (savedInstanceState != null) dismiss()
        dialog.setContentView(R.layout.dialog_actions_bottom_sheet)
        dialog.topTextView.text = title
        dialog.topTextView.isVisible = !title.isNullOrEmpty()
        dialog.actionsRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ActionsAdapter()
        adapter.onActionClickListener = { action ->
            action.onClickListener?.invoke()
            dismiss()
        }
        actions?.let { adapter.setActions(it) }
        dialog.actionsRecyclerView.attachAdapter(adapter)
        return dialog
    }

    override fun onStop() {
        super.onStop()
        dismissAllowingStateLoss()
    }
}
