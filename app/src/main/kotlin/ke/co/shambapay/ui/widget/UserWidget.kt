package ke.co.shambapay.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.databinding.WidgetUserBinding

class UserWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    internal val binding: WidgetUserBinding =
        WidgetUserBinding.inflate(LayoutInflater.from(context), this, true)

    fun setUp(user: UserEntity, settings: SettingsEntity) {
        binding.banner.setUp("Basic Information")
        binding.txtFullName.text = user.fetchFullName()
        binding.txtCompanyId.text = settings.companyId
        binding.txtCompanyName.text = settings.companyName
        binding.txtEmail.text = user.email
        binding.txtUserType.text = user.userType.name
        binding.txtPhone.text = if(user.phone != 0L) user.phone.toString() else "Not set"
    }

}