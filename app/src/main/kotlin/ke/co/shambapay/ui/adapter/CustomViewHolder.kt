package ke.co.shambapay.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.databinding.ListItemRecyclerBinding

class CustomViewHolder (
    private val binding: ListItemRecyclerBinding,
    private val listener: CustomAdapter.OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Any?){
        binding.widgetListEmployee.isVisible = false
        binding.widgetListWork.isVisible = false
        binding.widgetListUser.isVisible = false
        binding.widgetListJobRate.isVisible = false

        when (data){
            is EmployeeEntity -> {
                binding.widgetListEmployee.setUp(data)
                binding.widgetListEmployee.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListEmployee.isVisible = true
            }

            is JobRateEntity -> {
                binding.widgetListJobRate.setUp(data)
                binding.widgetListJobRate.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListJobRate.isVisible = true

            }

            is WorkEntity -> {
                binding.widgetListWork.setUp(data)
                binding.widgetListWork.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListWork.isVisible = true
            }

            is UserEntity -> {
                binding.widgetListUser.setUp(data)
                binding.widgetListUser.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListUser.isVisible = true
            }
        }

    }
}
