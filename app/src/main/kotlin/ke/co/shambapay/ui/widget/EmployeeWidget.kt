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
        binding.txtFullName.text = model.fetchFullName()
        binding.txtNationalId.text = model.fetchNationalId()
        binding.txtNhif.text = model.nhif.ifEmpty { "Not set" }
        binding.txtNssf.text = model.nssf.ifEmpty { "Not set" }
        binding.txtPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        val fullNumber = "${model.phone}"
        binding.txtPhone.text = fullNumber.ifEmpty { "Not set" }
    }

}