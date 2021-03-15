package namba.wallet.nambaone.uikit.actions

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_action_bottom_sheet.view.textView
import namba.wallet.nambaone.uikit.R

internal class ActionsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val actions = mutableListOf<Action>()
    var onActionClickListener: ((Action) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ActionViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val action = actions[position]
        (holder as ActionViewHolder).bind(action, onActionClickListener)
    }

    override fun getItemCount() = actions.size

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
        notifyDataSetChanged()
    }

    private class ActionViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_action_bottom_sheet, parent, false)) {

        fun bind(action: Action, onActionClickListener: ((Action) -> Unit)?) = with(itemView) {
            if (action.textRes != -1) {
                textView.setText(action.textRes)
            } else {
                textView.text = action.text
            }

            val tv = TypedValue()
            context.theme.resolveAttribute(action.textColorRes, tv, true)

            textView.setTextColor(tv.data)
            setOnClickListener { onActionClickListener?.invoke(action) }

            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(action.icon, null, null, null)
        }
    }
}
