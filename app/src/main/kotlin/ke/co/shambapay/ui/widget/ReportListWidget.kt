package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ke.co.shambapay.R
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.databinding.WidgetListReportBinding

class ReportListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListReportBinding =
        WidgetListReportBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: ReportEntity) {
        binding.txtItem.text = model.item
        binding.txtUnit.text = String.format("%,.2f", model.unit).trim()
        binding.txtUnit.isVisible = model.unit != 0.0
        if (model.isHeading){
            binding.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
        } else {
            binding.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        }
    }
}