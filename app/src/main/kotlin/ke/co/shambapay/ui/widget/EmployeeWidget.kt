package ke.co.shambapay.ui.widget

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.WidgetEmployeeBinding

class EmployeeWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetEmployeeBinding =
        WidgetEmployeeBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: EmployeeEntity) {
        binding.banner.setUp("Information")
        binding.txtFullName.text = model.getFullName()
        binding.txtNationalId.text = model.nationalId.toString()
        binding.txtNhif.text = model.nhif.toString()
        binding.txtNssf.text = model.nssf.toString()
        binding.txtPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        val fullNumber = "+${model.areaCode}${model.phone}"
        binding.txtPhone.text = fullNumber
    }

}