package com.google.android.fhir.demo.care.listscreening

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.care.CareUtil
import com.google.android.fhir.demo.databinding.ItemScreeningViewBinding
import org.hl7.fhir.r4.model.Task

class ScreeningLandingAdapter(private val clickHandle: ClickHandle) :
    RecyclerView.Adapter<ScreeningLandingAdapter.ScreeningLandingViewHolder>() {

    private val taskList = mutableListOf<Task>()

    fun setItemsList(taskList: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(taskList)
        notifyDataSetChanged()
    }

    inner class ScreeningLandingViewHolder(
        private val itemScreeningViewBinding: ItemScreeningViewBinding
    ) : RecyclerView.ViewHolder(itemScreeningViewBinding.root) {
        fun bind(task: Task) {
            itemScreeningViewBinding.title = CareUtil.getTaskName(task)
//            itemScreeningViewBinding.icon =
            itemScreeningViewBinding.status = task.status.display
//            itemScreeningViewBinding.statusColor =
            itemScreeningViewBinding.taskId = task.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreeningLandingViewHolder {
        return ScreeningLandingViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_screening_view,
                parent,
                false
            )
        ).listen { view, position, type -> clickHandle.onClick(position) }
    }

    override fun onBindViewHolder(holder: ScreeningLandingViewHolder, position: Int) {
        holder.bind(taskList.elementAt(position))
    }

    override fun getItemCount() = taskList.size
}

interface ClickHandle {
    fun onClick(position: Int)
}

fun <T : RecyclerView.ViewHolder> T.listen(
    event: (view: View, position: Int, type: Int) -> Unit
): T {
    itemView.setOnClickListener { event.invoke(itemView, adapterPosition, itemViewType) }
    return this
}