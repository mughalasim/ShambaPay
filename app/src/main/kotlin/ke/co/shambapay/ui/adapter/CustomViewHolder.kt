package ke.co.shambapay.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.data.model.*
import ke.co.shambapay.databinding.ListItemRecyclerBinding

class CustomViewHolder (
    private val binding: ListItemRecyclerBinding,
    private val listener: CustomAdapter.OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Any?){
        binding.widgetListEmployee.isVisible = false
        binding.widgetListJobRate.isVisible = false
        binding.widgetListReport.isVisible = false
        binding.widgetListCompany.isVisible = false
        binding.widgetListUser.isVisible = false

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

            is UserEntity -> {
                binding.widgetListUser.setUp(data)
                binding.widgetListUser.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListUser.isVisible = true
            }

            is BulkSMSData -> {
                binding.widgetListReport.setUp(ReportEntity(item = data.fullName + " - ${data.phone}", unit = data.amount))
                binding.widgetListReport.setOnClickListener { listener.onItemClicked(data) }
                binding.widgetListReport.isVisible = true
            }

        }

    }
}
