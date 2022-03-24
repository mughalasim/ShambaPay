package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.databinding.WidgetListWorkBinding
import ke.co.shambapay.utils.toParsedDate

class WorkListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListWorkBinding =
        WidgetListWorkBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: WorkEntity) {
        binding.txtDate.text = model.date.toString()
        binding.txtUnit.text = model.unit.toString()
    }

}