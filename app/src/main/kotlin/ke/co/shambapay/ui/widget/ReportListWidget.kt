package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.R
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.databinding.WidgetListJobRateBinding
import ke.co.shambapay.databinding.WidgetListReportBinding
import ke.co.shambapay.databinding.WidgetListWorkBinding
import ke.co.shambapay.utils.toParsedDate

class ReportListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListReportBinding =
        WidgetListReportBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: ReportEntity) {
        binding.txtItem.text = model.item
        binding.txtUnit.text = model.unit.toString()
    }
}