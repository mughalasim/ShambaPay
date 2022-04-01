package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.WidgetListEmployeeBinding

class EmployeeListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListEmployeeBinding =
        WidgetListEmployeeBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: EmployeeEntity) {
        binding.txtFullName.text = model.getFullName()
        binding.txtPhone.text = if (model.phone == 0L) model.phone.toString() else "Not available"
    }

}