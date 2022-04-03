package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.databinding.WidgetListCompanyBinding

class CompanyListWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetListCompanyBinding =
        WidgetListCompanyBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(model: CompanyEntity) {
        binding.txtFullName.text = model.Settings.companyName
        binding.txtCompanyId.text = model.Settings.companyId
        binding.img.isVisible = model.isDefault
    }

}