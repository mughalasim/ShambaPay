package ke.co.shambapay.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.databinding.ListItemRecyclerBinding

class CustomViewHolder (
    private val binding: ListItemRecyclerBinding,
    private val listener: CustomAdapter.OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Any?){
        binding.widgetListEmployee.isVisible = false
        binding.widgetListJobRate.isVisible = false
        binding.widgetListReport.isVisible = false
        binding.widgetListCompany.isVisible = false

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

            is ReportEntity -> {
                binding.widgetListReport.setUp(data)
                binding.widgetListReport.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListReport.isVisible = true
            }

            is CompanyEntity -> {
                binding.widgetListCompany.setUp(data)
                binding.widgetListCompany.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListCompany.isVisible = true
            }

        }

    }
}
