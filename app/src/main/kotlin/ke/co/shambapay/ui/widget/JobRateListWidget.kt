package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.R
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.databinding.WidgetListJobRateBinding
import ke.co.shambapay.databinding.WidgetListWorkBinding
import ke.co.shambapay.utils.toParsedDate

class JobRateListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListJobRateBinding =
        WidgetListJobRateBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: JobRateEntity) {
        binding.txtJobType.text = context.getString(R.string.txt_job_type, model.jobType.name)
        binding.txtJobRate.text = context.getString(R.string.txt_job_rate, model.rate.toString())
        binding.txtJobMeasurement.text = context.getString(R.string.txt_job_measured_in, model.measurement)
    }

}